package com.tcpip147.nodeformatter.ipc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcpip147.nodeformatter.setting.NodeSettingsState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class NodeJsProcess {

    private static final NodeJsProcess INSTANCE = new NodeJsProcess();

    public static NodeJsProcess getInstance() {
        return INSTANCE;
    }

    private NodeJsProcess() {
    }

    private static final Logger LOG = LoggerFactory.getLogger(NodeJsProcess.class);
    private static final String ROOT_DIR = System.getProperty("idea.plugins.path") + File.separator + "node-formatter";
    private ObjectMapper mapper = new ObjectMapper();
    private Process process;
    private BufferedReader br;
    private BufferedWriter bw;

    public void start() {
        String nodeExecutable = NodeSettingsState.getInstance().getNodeExecutable();
        String entry = ROOT_DIR + File.separator + "nodejs" + File.separator + "process.js";
        LOG.info("execute: {} {}", nodeExecutable, entry);
        ProcessBuilder processBuilder = new ProcessBuilder(nodeExecutable, entry);
        try {
            process = processBuilder.start();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            bw = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public NodeJsProtocol write(NodeJsProtocol request) {
        try {
            String q = mapper.writeValueAsString(request);
            bw.write(q);
            bw.write(0);
            bw.flush();
            StringBuffer sb = new StringBuffer();
            int c;
            while ((c = br.read()) != 0) {
                sb.append((char) c);
            }
            System.out.println(sb.toString());
            NodeJsProtocol protocol = mapper.readValue(sb.toString(), new TypeReference<>() {
            });
            return protocol;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
