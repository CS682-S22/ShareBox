package utils;

import java.util.List;

public class NodeConfig {
    public Node node;
    public List<String> downloadFiles;

    public Node getNode() {
        return node;
    }

    public List<String> getDownloadFiles() {
        return downloadFiles;
    }

    public String getHostname() {
        return node.hostname;
    }

    public String getIp() {
        return node.ip;
    }

    public int getPort() {
        return node.port;
    }

    public static class Node {
        public String hostname;
        public String ip;
        public int port;
    }
}
