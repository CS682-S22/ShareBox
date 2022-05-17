package utils;

import java.util.List;

public class NodeConfig {
    public Node node;
    public Node tracker;
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

    public String getTrackerHostname() {
        return tracker.hostname;
    }

    public String getTrackerIp() {
        return tracker.ip;
    }

    public int getTrackerPort() {
        return tracker.port;
    }

    public static class Node {
        public String hostname;
        public String ip;
        public int port;
    }
}
