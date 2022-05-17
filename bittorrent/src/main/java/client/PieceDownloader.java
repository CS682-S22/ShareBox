package client;

import models.Torrent;
import protos.Proto;
import protos.Response;
import utils.Connection;
import utils.ConnectionException;
import utils.Encryption;
import utils.Node;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PieceDownloader {
    private final Map<Node, Connection> connectionsMap;

    public PieceDownloader() {
        this.connectionsMap = new HashMap<>();
    }

    public void downloadPiece(Torrent torrent, Long pieceNumber, Node node) throws IOException {
        System.out.println("Requesting filename: " + torrent.name + ", pieceNumber: " + pieceNumber );
        Connection connection = this.connectionsMap.getOrDefault(node, new Connection(new Socket(node.getIp(), node.getPort())));
        this.connectionsMap.put(node, connection);
        Proto.Request request = Proto.Request.newBuilder().
                setRequestType(Proto.Request.RequestType.REQUEST_PIECE).
                setFileName(torrent.name).
                setPieceNumber(pieceNumber).
                build();
        try {
            connection.send(request.toByteArray());
            byte[] response = connection.receive();
            Response.PieceInfo pieceInfo = Response.PieceInfo.parseFrom(response);
            System.out.println("Response received - filename: " + pieceInfo.getFileName() + ", pieceNumber: " + pieceInfo.getPieceNumber());
            if (pieceInfo.getPieceNumber() == -1) {
                System.out.println("Seeder does not have the requested piece");
                return;
            }
            byte[] pieceHash = Encryption.encodeSHA1(pieceInfo.getPiece().toByteArray());
            if (!Arrays.toString(pieceHash).equals(torrent.pieces.get(pieceNumber))) {
                System.out.println("Hash does not match");
                System.out.println("Piece download unsuccessful");
                return;
            }
            System.out.println("Piece download successful");
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }
}
