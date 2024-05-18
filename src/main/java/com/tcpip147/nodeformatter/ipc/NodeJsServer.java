package com.tcpip147.nodeformatter.ipc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcpip147.nodeformatter.setting.NodeSettingsState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class NodeJsServer {

    private static final NodeJsServer INSTANCE = new NodeJsServer();

    public static NodeJsServer getInstance() {
        return INSTANCE;
    }

    private NodeJsServer() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(NodeJsServer.class);
    private static final String ROOT_DIR = System.getProperty("idea.plugins.path") + File.separator + "node-formatter";

    private ObjectMapper mapper = new ObjectMapper();
    private MessageReceiver messageReceiver;
    private MessageSender messageSender;
    private List<NodeJsDataListener> dataListenerList = new LinkedList<>();

    public void unzipJar() {
        LOG.info("ROOT_DIR: {}", ROOT_DIR);
        File jarFile = new File(ROOT_DIR + File.separator + "lib" + File.separator + "instrumented-node-formatter-1.0-SNAPSHOT.jar");
        LOG.info("jarFile: {}", jarFile);
        File destinationDir = new File(ROOT_DIR);
        byte[] buff = new byte[1024];

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(jarFile))) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                if (entry.getName().startsWith("nodejs/")) {
                    File newFile = new File(destinationDir, entry.getName());
                    if (entry.isDirectory()) {
                        if (!newFile.exists()) {
                            newFile.mkdirs();
                        }
                    } else {
                        File parent = newFile.getParentFile();
                        if (!parent.exists()) {
                            parent.mkdirs();
                        }
                        FileOutputStream fos = new FileOutputStream(newFile);
                        int len;
                        while ((len = zis.read(buff)) > 0) {
                            fos.write(buff, 0, len);
                        }
                        fos.close();
                    }
                }
                entry = zis.getNextEntry();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    public void start() {
        String nodeExecutable = NodeSettingsState.getInstance().getNodeExecutable();
        String entry = ROOT_DIR + File.separator + "nodejs" + File.separator + "server.js";
        LOG.info("execute: {} {}", nodeExecutable, entry);
        ProcessBuilder processBuilder = new ProcessBuilder(nodeExecutable, entry);
        Process process = null;
        try {
            process = processBuilder.start();
            InputStream is = process.getInputStream();
            byte[] b = new byte[16];
            int len = is.read(b);
            int port = Integer.parseInt(new String(b, 0, len - 1));
            Socket socket = new Socket("127.0.0.1", port);
            LOG.info("connected to port: {}", port);
            messageReceiver = new MessageReceiver(socket);
            messageSender = new MessageSender(socket);
            messageReceiver.start();
            messageSender.start();
        } catch (Exception e) {
            LOG.error(e.getMessage());
            messageReceiver.interrupt();
            messageSender.interrupt();
            if (process != null) {
                process.destroy();
            }
        }
    }

    public void addDataListener(NodeJsDataListener dataListener) {
        dataListenerList.add(dataListener);
    }

    public void removeDataListener(NodeJsDataListener dataListener) {
        dataListenerList.remove(dataListener);
    }

    private class MessageReceiver extends Thread {

        private BufferedReader br;

        public MessageReceiver(Socket socket) throws IOException {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            StringBuffer sb = new StringBuffer();
            int c;
            while (true) {
                try {
                    while ((c = br.read()) != 0) {
                        sb.append((char) c);
                    }
                    NodeJsProtocol protocol = mapper.readValue(sb.toString(), new TypeReference<>() {
                    });
                    for (NodeJsDataListener listener : dataListenerList) {
                        listener.listen(protocol);
                    }
                    sb.setLength(0);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private class MessageSender extends Thread {

        private BufferedWriter bw;

        public MessageSender(Socket socket) throws IOException {
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }

        public void write(String data) throws IOException {
            bw.write(data);
            bw.write(0);
            bw.flush();
        }
    }

    public void write(NodeJsProtocol request) {
        try {
            messageSender.write(mapper.writeValueAsString(request));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
