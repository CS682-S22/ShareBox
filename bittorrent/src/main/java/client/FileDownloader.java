package client;

import com.google.protobuf.InvalidProtocolBufferException;
import models.Torrent;
import protos.Proto;
import protos.Response;
import protos.Response.PeersList;
import utils.ConnectionException;
import utils.Helper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author Alberto Delgado on 5/16/22
 * @project bittorrent
 */
public class FileDownloader implements Runnable {
    private final Client client;
    private final Torrent torrent;
    private final PieceDownloader pieceDownloader;
    private final byte[] file;
    private Map<Long, PeersList> peers;
    private boolean isDone = false;

    public FileDownloader(Client client, Torrent torrent) {
        this.client = client;
        this.torrent = torrent;
        this.pieceDownloader = new PieceDownloader();
        this.file = new byte[Math.toIntExact(torrent.totalSize)];
    }

    public void download() throws ConnectionException, IOException, ExecutionException, InterruptedException {
        String filename = torrent.getName();
        System.out.println("Downloading " + filename);

        Map<Long, PeersList> piecesInfo = getPiecesInformation(filename);

        if (piecesInfo == null) {
            System.out.println("No response from Tracker");
            return;
        } else if (piecesInfo.size() == 0) {
            System.out.println("No seeder currently seeding " + filename);
            return;
        } else if (piecesInfo.containsKey(-1L) && piecesInfo.size() == 1) {
            System.out.println("No seeders currently seeding " + filename);
            return;
        }

        System.out.println("Response: " + piecesInfo);
        Set<Map.Entry<Long, PeersList>> piecesEntrySets = piecesInfo.entrySet();

        // do not download already downloaded pieces.
        // this algo has plenty of room for improvement.
        piecesEntrySets.removeIf(entry -> torrent.hasPiece(entry.getKey()));

        // sorted pieces by rarest first
        List<Map.Entry<Long, PeersList>> rarestFirst = new ArrayList<>(piecesEntrySets);
        rarestFirst.sort((a, b) -> {
            int sizeA = a.getValue().getSerializedSize();
            int sizeB = b.getValue().getSerializedSize();
            return sizeA - sizeB;
        });

        while (!rarestFirst.isEmpty()) {
            Map<Long, byte[]> downloaded = pieceDownloader.downloadPieces(torrent, rarestFirst);

            // remove downloaded pieces from rarest first list
            // update torrent information
            for (Map.Entry<Long, byte[]> piece : downloaded.entrySet()) {
                for (Map.Entry<Long, PeersList> item : rarestFirst)
                    if (Objects.equals(item.getKey(), piece.getKey())) {
                        rarestFirst.remove(item);
                        torrent.addDownloadedPiece(piece.getKey());
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

    @Override
    public void run() {
        try {
            download();
        } catch (ConnectionException | IOException | InterruptedException | ExecutionException ignored) {
            // ignored
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
