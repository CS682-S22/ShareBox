package client;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import models.Torrent;
import protos.Proto;
import protos.Response;
import utils.Connection;
import utils.ConnectionException;
import utils.FileIO;

public class ConnectionHandler implements Runnable{
    private final Connection connection;
    private final Library library;

    public ConnectionHandler(Library library, Connection connection) {
        this.library = library;
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
        String fileName = request.getFileName();
        long pieceNumber = request.getPieceNumber();
        System.out.println("\nPiece Request received: filename: " + fileName + ", pieceNumber: " + pieceNumber );
        Response.PieceInfo.Builder responseBuilder = Response.PieceInfo.newBuilder();
        Torrent torrent = this.library.getTorrent(fileName);
        if (torrent == null) {
            System.out.println("Torrent not found");
            pieceNumber = -1;
        }
        else {
            byte[] piece = FileIO.getInstance().readPiece(torrent, pieceNumber);
            responseBuilder.setPiece(ByteString.copyFrom(piece));
            responseBuilder.setPieceHash(ByteString.copyFromUtf8(torrent.pieces.get(pieceNumber)));
        }
        responseBuilder.setPieceNumber(pieceNumber).setFileName(fileName);
        // Need to get requestedpiece from file
        // Need to set piece number to -1 if piece/file does not exist

        Response.PieceInfo response = responseBuilder.build();
        this.connection.send(response.toByteArray());
    }

    @Override
    public void run() {
        System.out.println("\n[PEER] New connection!");
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
