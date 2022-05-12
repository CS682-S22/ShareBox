package client;

import models.Torrent;
import utils.*;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
public class BootUp {
    public static void start() throws ConnectionException {
        List<Torrent> torrents = getTorrents();
        if (torrents == null) return;

        Connection trackerConn = getTrackerConnection();
        if (trackerConn == null) throw new ConnectionException("Could not connect to Tracker");
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
            return new Connection(new Socket(Globals.trackerIP, Integer.parseInt(Globals.trackerPort)));
        } catch (IOException e) {
            return null;
        }
    }

}
