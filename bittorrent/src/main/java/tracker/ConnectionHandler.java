package tracker;

import com.google.protobuf.InvalidProtocolBufferException;
import protos.Node.NodeDetails;
import protos.Proto.Request;
import protos.Response;
import protos.Response.FileInfo;
import utils.Connection;
import utils.ConnectionException;
import utils.Helper;
import utils.Node;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConnectionHandler implements Runnable {
    private final Connection connection;
    private final Tracker tracker;

    public ConnectionHandler(Tracker tracker, Connection connection) {
        this.tracker = tracker;
        this.connection = connection;
    }

    private Request receiveRequest() {
        byte[] message = this.connection.receive();
        if (message == null) return null;

        try {
            return Request.parseFrom(message);
        } catch (InvalidProtocolBufferException e) {
            System.out.println("Invalid packet received");
            e.printStackTrace();
            return null;
        }
    }

    private void serveRequestPeers(Request request) throws ConnectionException {
        System.out.println("Request received: " + request);
        String fileName = request.getFileName();
        Map<Long, List<NodeDetails>> fileInfo = this.tracker.getFileInfo(fileName);
        System.out.println("fileinfo from database: " + fileInfo);
        FileInfo.Builder responseBuilder = FileInfo.newBuilder();
        if (fileInfo != null) {
            for (Map.Entry<Long, List<NodeDetails>> item : fileInfo.entrySet()) {
                Long pieceNumber = item.getKey();
                Response.PeersList peersList = Response.PeersList.newBuilder().
                        addAllNodes(item.getValue()).
                        build();
                responseBuilder.putPiecesInfo(pieceNumber, peersList);
            }
        }
        else {
            NodeDetails nullNode = Helper.getNodeDetailsObject(new Node("null", "null", 0));
            Response.PeersList peersList = Response.PeersList.newBuilder().
                    addNodes(nullNode).
                    build();
            responseBuilder.putPiecesInfo(-1, peersList);
        }
        FileInfo response = responseBuilder.build();
        this.connection.send(response.toByteArray());
    }

    private void addPeer(Request request) {
        NodeDetails n = request.getNode();
        Node peer = new Node(n.getHostname(), n.getIp(), n.getPort());

        // missing to add peer torrents information
        // to swarm database

        this.tracker.addPeer(peer);
    }

    @Override
    public void run() {
        System.out.println("New connection!");
        while (!this.connection.isClosed()) {
            Request request = receiveRequest();
            if (request == null) continue;

            Request.RequestType requestType = request.getRequestType();
            if (requestType.equals(Request.RequestType.REQUEST_PEERS)) {
                try {
                    this.serveRequestPeers(request);
                } catch (ConnectionException e) {
                    e.printStackTrace();
                }
            } else if (requestType.equals(Request.RequestType.PEER_MEMBERSHIP)) {
                this.addPeer(request);
            }
        }
    }
}
