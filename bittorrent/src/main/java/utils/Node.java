package utils;

public class Node {
    private final String hostname;
    private final String ip;
    private final int port;
    private Thread serverThread;
    private boolean isServerRunning;

    public Node(String hostname, String ip, int port) {
        this.hostname = hostname;
        this.ip = ip;
        this.port = port;
        this.isServerRunning = false;
    }

    public String getHostname() {
        return hostname;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    protected void initializeServer(Runnable serverObj) {
        this.serverThread = new Thread (serverObj);
    }

    public void startServer() {
        this.isServerRunning = true;
        this.serverThread.start();
    }

    public void stopServer() {
        this.isServerRunning = false;
    }
}
