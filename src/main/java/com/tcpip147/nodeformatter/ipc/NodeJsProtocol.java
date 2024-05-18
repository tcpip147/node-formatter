package com.tcpip147.nodeformatter.ipc;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class NodeJsProtocol {

    private String processType;
    private String languageType;
    private String filename;
    private Map<String, Object> data = new HashMap<>();
    private String error;

    public NodeJsProtocol() {
    }

    public NodeJsProtocol(String processType, String language, String filename) {
        this.processType = processType;
        this.languageType = language;
        this.filename = filename;
    }

    public void putData(String key, Object value) {
        data.put(key, value);
    }

    public Object getData(String key) {
        return data.get(key);
    }
}
