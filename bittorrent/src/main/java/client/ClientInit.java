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
 * @author anchit bhatia
 * @project bittorrent
 * <p>
 * Logic to be performed on Client boot up. This is expected to be run
 * only once.
 */
public class ClientInit {
    private static List<Torrent> localTorrents = new ArrayList<>();
    private static final FileIO fileIO = FileIO.getInstance();

    /**
     * Joins the bittorrent swarm
     *
     * @param hostname hostname
     * @param ip       ip
     * @param port     port
     * @return void
     * @throws ConnectionException exception
     */
    public static Connection joinSwarm(String hostname, String ip, int port) throws ConnectionException {
        Connection trackerConn = getTrackerConnection();
        if (trackerConn == null) throw new ConnectionException("Could not connect to Tracker");
        System.out.println("Hostname: " + hostname + ", Ip: " + ip + ", Port: " + port);
        Proto.Request request = createRequest(hostname, ip, port, localTorrents);
        List<Proto.Torrent> torrents = request.getTorrentsList();
        for (Proto.Torrent torrent : torrents) {
            System.out.println("Name: " + torrent.getFilename());
            System.out.println("Total pieces: " + torrent.getPiecesList().size());
        }
        byte[] requestMessage = request.toByteArray();
        trackerConn.send(requestMessage);
        // "files" are only used on clientInit, so only when the client
        // is initialized. Therefore, we want garbage collector to get rid
        // of files to avoid memory leaks.
        localTorrents = null;
        return trackerConn;
    }

    /**
     * Scans local torrents and checks what pieces user has of
     * those files. Can range from none to all of them
     *
     * @return
     */
    public static Library initLibrary() {
        List<Torrent> torrents = getTorrents();
        Library library = new Library();
        if (torrents == null) return library;

        for (Torrent t : torrents)
            localTorrents.add(library.add(t));

        return library;
    }

    /**
     * Added for testing purposes
     *
     * @param testing
     * @return
     */
    public static Library initLibrary(boolean testing) {
        fileIO.testing();
        return initLibrary();
    }

    /**
     * Gets local torrents
     *
     * @return
     */
    private static List<Torrent> getTorrents() {
        try {
            List<byte[]> torrents = fileIO.readTorrents();
            return torrents.stream()
                    .map(TCodec::decode)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Connects to remote tracker
     *
     * @return
     */
    private static Connection getTrackerConnection() {
        try {
            return new Connection(new Socket(Globals.trackerIP, Globals.trackerPort));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Membership request: Creates the request to be sent to tracker to join swarm.
     *
     * @param hostname
     * @param ip
     * @param port
     * @param torrents
     * @return
     */
    private static Proto.Request createRequest(String hostname, String ip, int port, List<Torrent> torrents) {
        return Proto.Request.newBuilder()
                .setRequestType(Proto.Request.RequestType.PEER_MEMBERSHIP)
                .setNode(
                        Node.NodeDetails.newBuilder()
                                .setHostname(hostname)
                                .setIp(ip)
                                .setPort(port)
                                .build()
                )
                .addAllTorrents(torrents != null ? torrents.stream()
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
                        .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }
}
