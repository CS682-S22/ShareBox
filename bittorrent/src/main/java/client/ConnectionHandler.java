package client;

import com.google.protobuf.InvalidProtocolBufferException;
import protos.Node;
import protos.Proto;
import protos.Response;
import utils.Connection;
import utils.ConnectionException;
import utils.Helper;

import java.util.List;
import java.util.Map;

public class ConnectionHandler implements Runnable{
    private final Connection connection;

    public ConnectionHandler(Connection connection) {
        this.connection = connection;
    }

    private Proto.Request receiveRequest() {
        byte[] message = this.connection.receive();
        if (message == null) return null;

        try {
            return Proto.Request.parseFrom(message);
        } catch (InvalidProtocolBufferException e) {
            System.out.println("[TRACKER] Invalid packet received");
            e.printStackTrace();
            return null;
        }
    }

    private void serveRequestPiece(Proto.Request request) throws ConnectionException {
        System.out.println("Request received: " + request);
        String fileName = request.getFileName();
        long pieceNumber = request.getPieceNumber();

        // Need to get requestedpiece from file
        // Need to set piece number to -1 if piece/file does not exist

        Response.PieceInfo response = Response.PieceInfo.newBuilder().
                setFileName(fileName).
                setPieceNumber(pieceNumber).build();
        this.connection.send(response.toByteArray());
    }

    @Override
    public void run() {
        System.out.println("[PEER] New connection!");
        while (!this.connection.isClosed()) {
            Proto.Request request = receiveRequest();
            if (request == null) continue;

            Proto.Request.RequestType requestType = request.getRequestType();
            if (requestType.equals(Proto.Request.RequestType.REQUEST_PIECE)) {
                try {
                    this.serveRequestPiece(request);
                } catch (ConnectionException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
