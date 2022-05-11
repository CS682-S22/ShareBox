package tracker;

import utils.Connection;
import utils.Node;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Alberto Delgado on 5/9/22
 * @project dsd-final-project-anchitbhatia
 */
public class Tracker extends Node {

    public Tracker(String hostname, String ip, int port) throws IOException {
        super(hostname, ip, port);
        this.initializeServer(new TrackerServer());
    }

    private class TrackerServer implements Runnable {
        
        private final ExecutorService connectionPool;

        public TrackerServer() {
            this.connectionPool = Executors.newCachedThreadPool();
        }

        @Override
        public void run() {
            try {
                while (isServerRunning) {
                    Socket clientSocket = serverSocket.accept();
                    Connection connection = new Connection(clientSocket);
                    this.connectionPool.execute(new ClientHandler(connection));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
