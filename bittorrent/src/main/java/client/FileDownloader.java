package client;

import com.google.protobuf.InvalidProtocolBufferException;
import models.Torrent;
import protos.Node;
import protos.Proto;
import protos.Response;
import protos.Response.PeersList;
import utils.ConnectionException;
import utils.Helper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author Alberto Delgado on 5/16/22
 * @project bittorrent
 */
public class FileDownloader implements Runnable {
    private final Client client;
    private final Torrent torrent;
    private final PieceDownloader pieceDownloader;
    private Map<Long, PeersList> peers;
    private boolean isDone = false;

    public FileDownloader(Client client, Torrent torrent) {
        this.client = client;
        this.torrent = torrent;
        this.pieceDownloader = new PieceDownloader();
    }

    public void download() throws ConnectionException, IOException, ExecutionException, InterruptedException {
        String filename = torrent.getName();
        System.out.println("Downloading " + filename);

        Map<Long, PeersList> piecesInfo = getPiecesInformation(filename);
        if (piecesInfo == null) {
            System.out.println("No response from Tracker");
        } else if (piecesInfo.size() == 0) {
            System.out.println("No seeder currently seeding " + filename);
        } else if (piecesInfo.containsKey(-1L) && piecesInfo.size() == 1) {
            System.out.println("No seeders currently seeding " + filename);
        } else {
            System.out.println("Response: " + piecesInfo);
            for (Map.Entry<Long, PeersList> item : piecesInfo.entrySet()) {
                List<Node.NodeDetails> peers = item.getValue().getNodesList();
                if (peers.size() > 0) {
                    pieceDownloader.downloadPiece(torrent, item.getKey(), Helper.getNodeObject(peers.get(0)));
                }
            }
        }

        isDone = true;
    }

    private Map<Long, PeersList> getPiecesInformation(String fileName) throws ConnectionException {
        Proto.Request request = Proto.Request.newBuilder().
                setNode(Helper.getNodeDetailsObject(client)).
                setRequestType(Proto.Request.RequestType.REQUEST_PEERS).
                setFileName(fileName)
                .build();

        client.trackerConnection.send(request.toByteArray());
        byte[] response = client.trackerConnection.receive();
        try {
            Response.FileInfo fileInfo = Response.FileInfo.parseFrom(response);
            return fileInfo.getPiecesInfoMap();
        } catch (InvalidProtocolBufferException e) {
            return null;
        }
    }

    public boolean isDone() {
        return isDone;
    }

    public boolean connect() {

    }

    @Override
    public void run() {
        try {
            download();
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//    public void sendTorrentInfo(Torrent torrent) throws ConnectionException {
//        System.out.println("Sending torrent info: " + torrent.getName());
//
//        Proto.Torrent torrentMsg = Proto.Torrent.newBuilder().
//                setFilename(torrent.name).
//                addAllPieces(torrent.pieces).
//                build();
//        Proto.Request request = Proto.Request.newBuilder().
//                setNode(Helper.getNodeDetailsObject(this)).
//                setRequestType(Proto.Request.RequestType.PEER_MEMBERSHIP).
//                setFileName(torrent.name).
//                addTorrents(torrentMsg).
//                build();
//        trackerConnection.send(request.toByteArray());
//    }
}
