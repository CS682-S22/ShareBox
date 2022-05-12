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
    private static List<Library.File> files = new ArrayList<>();

    public static Connection joinSwarm(String hostname, String ip, int port) throws ConnectionException {
        Connection trackerConn = getTrackerConnection();
        if (trackerConn == null) throw new ConnectionException("Could not connect to Tracker");

        byte[] requestMessage = createRequest(hostname, ip, port, files).toByteArray();
        trackerConn.send(requestMessage);
        files = null; // after initializing, let garbage collector do its jobs
        return trackerConn;
    }

    public static Library initLibrary() {
        List<Torrent> torrents = getTorrents();
        Library library = new Library();
        for (Torrent t : torrents)
            files.add(library.add(t));

        return library;
    }

    private static List<Torrent> getTorrents() {
        try {
            List<byte[]> files = FileIO.getInstance().readTorrents();
            return files.stream()
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

    private static Proto.Request createRequest(String hostname, String ip, int port, List<Library.File> torrents) {
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
                                .addAllPieces(t.pieces)
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
