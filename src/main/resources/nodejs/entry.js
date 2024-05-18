const net = require('net');
const prettier = require("prettier");

const server = net.createServer(function(sock) {
  let buffer = Buffer.alloc(0);
  sock.on('data', data => {
    buffer = Buffer.concat([buffer, data]);
    let nullByteIndex;
    while ((nullByteIndex = buffer.indexOf(0)) !== -1) {
        const request = JSON.parse(buffer.slice(0, nullByteIndex));
        processRequest(request, sock);
        buffer = buffer.slice(nullByteIndex + 1);
    }
  });
});

server.listen(0, function() {
  console.log(server.address().port);
});

function sendResponse(sock, response) {
    sock.write(JSON.stringify(response));
    sock.write('\x00');
}

function processRequest(request, sock) {
    if (request.processType == "FORMATTER") {
        processFormatter(request, sock);
    } else {
        request.error = "Cannot process processType";
        sendResponse(sock, request);
    }
}

function processFormatter(request, sock) {
    let parser;
    if (request.languageType == "CSS") {
        parser = "css";
    } else if (request.languageType == "JAVASCRIPT") {
        parser = "typescript";
    } else if (request.languageType == "HTML") {
        parser = "html";
    } else {
        request.error = "Cannot process languageType";
        sendResponse(sock, request);
    }

    prettier.format(request.data.text, { parser: parser }).then(res => {
        request.data = {
            text: res
        };
        sendResponse(sock, request);
    }).catch(e => {
        request.error = e.message;
        sendResponse(sock, request);
    });
}