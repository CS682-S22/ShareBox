package tracker;

import protos.Node.NodeDetails;
import protos.Proto;
import utils.Connection;
import utils.Constants.Status;
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
    final NodeDetector nodeDetector;

    public Tracker(String hostname, String ip, int port) throws IOException {
        super(hostname, ip, port);
        initializeServer(new TrackerServer(this));
        this.swarmDatabase = new SwarmDatabase();
        this.nodeDetector = new NodeDetector(swarmDatabase);
    }

    protected Map<Long, List<NodeDetails>> getFileInfo(String fileName) {
        return this.swarmDatabase.getFileInfo(fileName);
    }

    protected Map<String, Node> getPeerList() {
        return this.swarmDatabase.getPeersList();
    }

    protected Map<String, Status> getPeerStatus() {
        return this.swarmDatabase.getPeerStatus();
    }

    protected void addPeer(Node node, List<Proto.Torrent> torrents) {
        this.swarmDatabase.addPeer(node);
        for (Proto.Torrent t : torrents)
            for (long i = 0; i < t.getPiecesList().size(); i++)
                this.swarmDatabase.addPieceInfo(t.getFilename(), i, node);
    }

    public void heartbeatReceived(NodeDetails node) {
        nodeDetector.heartbeatReceived(node);
    }

    void addPieceInfo(String fileName, Long pieceNumber, Node node) {
        this.swarmDatabase.addPieceInfo(fileName, pieceNumber, node);
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
