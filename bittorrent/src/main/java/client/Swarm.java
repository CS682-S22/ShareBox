package client;

import models.Torrent;
import protos.Proto;
import utils.*;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
public class Swarm {
    public static void join() throws ConnectionException {
        List<Torrent> torrents = getTorrents();
        if (torrents == null) return;

        Connection trackerConn = getTrackerConnection();
        if (trackerConn == null) throw new ConnectionException("Could not connect to Tracker");

        trackerConn.send(createRequest(torrents).toByteArray());
    }

    static List<Torrent> getTorrents() {
        try {
            List<byte[]> files = FileIO.getInstance().readTorrents();
            return files.stream()
                    .map(TCodec::decode)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return null;
        }
    }

    static Connection getTrackerConnection() {
        try {
            return new Connection(new Socket(Globals.trackerIP, Globals.trackerPort));
        } catch (IOException e) {
            return null;
        }
    }

    static Proto.Request createRequest(List<Torrent> torrents) {
        return Proto.Request.newBuilder()
                .setRequestType(Proto.Request.RequestType.PEER_MEMBERSHIP)
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
