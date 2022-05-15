package tracker;

import protos.Node.NodeDetails;
import utils.Connection;
import utils.Node;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Alberto Delgado on 5/9/22
 * @project dsd-final-project-anchitbhatia
 */
public class Tracker extends Node {
    final SwarmDatabase swarmDatabase;

    public Tracker(String hostname, String ip, int port) throws IOException {
        super(hostname, ip, port);
        initializeServer(new TrackerServer(this));
        this.swarmDatabase = new SwarmDatabase();
    }

    protected Map<Long, List<NodeDetails>> getFileInfo(String fileName) {
        return this.swarmDatabase.getFileInfo(fileName);
    }

    protected void addPeer(Node node) {
        this.swarmDatabase.addPeer(node);
    }

    private class TrackerServer implements Runnable {
        private final Tracker tracker;
        private final ExecutorService connectionPool;

        public TrackerServer(Tracker tracker) {
            this.tracker = tracker;
            this.connectionPool = Executors.newCachedThreadPool();
        }

        @Override
        public void run() {
            try {
                System.out.println("Starting server on trackerNode at " + port);
                while (isServerRunning) {
                    Socket clientSocket = serverSocket.accept();
                    Connection connection = new Connection(clientSocket);
                    this.connectionPool.execute(new ConnectionHandler(this.tracker, connection));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
