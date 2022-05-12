package tracker;

import com.google.protobuf.InvalidProtocolBufferException;

import protos.Node.NodeDetails;
import protos.Proto.Request;
import protos.Response;
import protos.Response.FileInfo;
import utils.Connection;
import utils.ConnectionException;

import java.util.List;
import java.util.Map;

public class ConnectionHandler implements Runnable {
    private final Connection connection;
    private final Tracker tracker;

    public ConnectionHandler(Tracker tracker, Connection connection) {
        this.tracker = tracker;
        this.connection = connection;
    }

    private void serveRequestPeers(Request request) throws ConnectionException {
        String fileName = request.getFileName();
        Map<Long, List<NodeDetails>> fileInfo = this.tracker.getFileInfo(fileName);
        FileInfo.Builder responseBuilder = FileInfo.newBuilder();
        if (fileInfo!=null) {
            for (Map.Entry<Long, List<NodeDetails>> item : fileInfo.entrySet()) {
                Long pieceNumber = item.getKey();
                Response.PeersList peersList = Response.PeersList.newBuilder().
                        addAllNodes(item.getValue()).
                        build();
                responseBuilder.putPiecesInfo(pieceNumber, peersList);
            }
        }
        FileInfo response = responseBuilder.build();
        this.connection.send(response.toByteArray());
    }

    @Override
    public void run() {
        while (!this.connection.isClosed()) {
            byte[] message = this.connection.receive();
            if (message!=null) {
                try {
                    Request request = Request.parseFrom(message);
                    if (request.getRequestType().equals(Request.RequestType.REQUEST_PEERS)) {
                        this.serveRequestPeers(request);
                    }
                } catch (InvalidProtocolBufferException e) {
                    System.out.println("Invalid packet received");
                    e.printStackTrace();
                } catch (ConnectionException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
