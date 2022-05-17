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
 * @author anchit bhatia
 * @project dsd-final-project-anchitbhatia
 * <p>
 * Tracker server.
 * <p>
 * Stores information of peers, shared files and peers owning pieces
 * of each file. Handles heartbeats to check online/offline peers.
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

    /**
     * File information getter
     *
     * @param fileName
     * @return
     */
    protected Map<Long, List<NodeDetails>> getFileInfo(String fileName) {
        return this.swarmDatabase.getFileInfo(fileName);
    }

    /**
     * Peer list getter
     *
     * @return
     */
    protected Map<String, Node> getPeerList() {
        return this.swarmDatabase.getPeersList();
    }

    /**
     * Peer status getter
     *
     * @return
     */
    protected Map<String, Status> getPeerStatus() {
        return this.swarmDatabase.getPeerStatus();
    }

    /**
     * Peer setter
     *
     * @param node
     * @param torrents
     */
    protected void addPeer(Node node, List<Proto.Torrent> torrents) {
        this.swarmDatabase.addPeer(node);
    }

    /**
     * Handles new received heartbeat
     *
     * @param node
     */
    public void heartbeatReceived(NodeDetails node) {
        nodeDetector.heartbeatReceived(node);
    }

    /**
     * New piece information setter
     *
     * @param fileName
     * @param pieceNumber
     * @param node
     */
    void addPieceInfo(String fileName, Long pieceNumber, Node node) {
        this.swarmDatabase.addPeer(node);
        this.swarmDatabase.addPieceInfo(fileName, pieceNumber, node);
    }

    /**
     * Handles local server
     */
    private class TrackerServer implements Runnable {
        private final Tracker tracker;
        private final ExecutorService connectionPool;

        public TrackerServer(Tracker tracker) {
            this.tracker = tracker;
            this.connectionPool = Executors.newCachedThreadPool();
        }

        /**
         * Handles connections in a connection pool
         */
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
