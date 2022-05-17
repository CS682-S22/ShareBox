package client;

import models.Torrent;
import protos.Node;
import protos.Proto;
import utils.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
public class ClientInit {
    private static List<Torrent> torrentDetails = new ArrayList<>();

    public static Connection joinSwarm(String hostname, String ip, int port) throws ConnectionException {
        Connection trackerConn = getTrackerConnection();
        if (trackerConn == null) throw new ConnectionException("Could not connect to Tracker");
        byte[] requestMessage = createRequest(hostname, ip, port, torrentDetails).toByteArray();
        trackerConn.send(requestMessage);
        // "files" are only used on clientInit, so only when the client
        // is initialized. Therefore, we want garbage collector to get rid
        // of files to avoid memory leaks.
        torrentDetails = null;
        return trackerConn;
    }

    public static List<Torrent> getTorrentDetails() {
        return torrentDetails;
    }

    public static Library initLibrary() {
        List<Torrent> torrents = getTorrents();
        Library library = new Library();
        if (torrents == null) return library;

        for (Torrent t : torrents)
            torrentDetails.add(library.add(t));

        return library;
    }

    private static List<Torrent> getTorrents() {
        try {
            List<byte[]> torrents = FileIO.getInstance().readTorrents();
            return torrents.stream()
                    .map(TCodec::decode)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return null;
        }
    }

    private static Connection getTrackerConnection() {
        try {
            return new Connection(new Socket(Globals.trackerIP, Globals.trackerPort));
        } catch (IOException e) {
            return null;
        }
    }

    private static Proto.Request createRequest(String hostname, String ip, int port, List<Torrent> torrents) {
        System.out.println("Sending request with torrents: " + torrents.size());
        return Proto.Request.newBuilder()
                .setRequestType(Proto.Request.RequestType.PEER_MEMBERSHIP)
                .setNode(
                        Node.NodeDetails.newBuilder()
                                .setHostname(hostname)
                                .setIp(ip)
                                .setPort(port)
                                .build()
                )
                .addAllTorrents(torrents.stream()
                        .map((t) -> Proto.Torrent.newBuilder()
                                .setFilename(t.name)
                                .setPieceLength(t.pieceLength)
                                .addAllPieces(t.downloadedPieces)
                                .setSingleFileTorrent(t.singleFileTorrent)
                                .setTotalSize(t.totalSize)
                                .setComment(t.comment)
                                .setCreatedBy(t.createdBy)
                                .setCreationDate(t.creationDate.getTime())
                                .setInfoHash(t.infoHash)
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
