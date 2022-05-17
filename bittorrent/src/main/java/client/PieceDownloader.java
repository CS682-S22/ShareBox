package client;

import models.Torrent;
import protos.Node.NodeDetails;
import protos.Proto;
import protos.Response;
import protos.Response.PeersList;
import utils.*;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

import static utils.Constants.MAX_CONCURRENT_DOWNLOADS;

public class PieceDownloader {
    private final Map<Node, Connection> connectionsMap;

    public PieceDownloader() {
        this.connectionsMap = new HashMap<>();
    }

    public Map<Long, byte[]> downloadPieces(Client client, Torrent torrent, List<Map.Entry<Long, PeersList>> pieces) {
        Map<Long, byte[]> data = new HashMap<>();

        Set<NodeDetails> assignedPeers = new HashSet<>();
        for (Map.Entry<Long, Response.PeersList> item : pieces) {
            if (assignedPeers.size() >= MAX_CONCURRENT_DOWNLOADS) break;

            List<NodeDetails> peers = item.getValue().getNodesList();
            long pieceNumber = item.getKey();
            if (peers.size() == 0) continue;

            try {
                Node peer = null;
                for (NodeDetails node : peers) {
                    if (node.getIp().equals(client.getIp())
                            && node.getPort() == client.getPort())
                        continue;
                    if (!assignedPeers.contains(node)) {
                        assignedPeers.add(node);
                        peer = Helper.getNodeObject(peers.get(0));
                        break;
                    }
                }
                if (peer == null) continue;
                byte[] piece = downloadPiece(torrent, item.getKey(), peer);
                data.put(pieceNumber, piece);
            } catch (IOException e) {
                // continue
            }
        }

        return data;
    }

    private byte[] downloadPiece(Torrent torrent, Long pieceNumber, Node node) throws IOException {
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
                return null;
            }

            byte[] pieceHash = Encryption.encodeSHA1(pieceInfo.getPiece().toByteArray());
            if (!Arrays.toString(pieceHash).equals(torrent.pieces.get(pieceNumber))) {
                System.out.println("Hash does not match");
                System.out.println("Piece download unsuccessful");
                return null;
            }
            System.out.println("Piece download successful");
            return pieceInfo.getPiece().toByteArray();
        } catch (ConnectionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
