import client.Client;
import models.Torrent;
import tracker.Tracker;
import utils.*;

import java.io.IOException;
import java.util.List;

/**
 * @author Alberto Delgado on 5/9/22
 * @author anchit bhatia

 * @project dsd-final-project-anchitbhatia
 * <p>
 * Example app. Has:
 * - Client (leecher)
 * - Client (seeder)
 * - Tracker (announcer)
 */
public class App {

    /**
     * Creates a client node
     *
     * @param config
     */
    private static void clientNode(NodeConfig config) {
        try {
            Globals.trackerIP = config.getTrackerIp();
            Globals.trackerPort = config.getTrackerPort();
            Globals.trackerName = config.getTrackerHostname();
            Client client = new Client(config.getHostname(), config.getIp(), config.getPort());
            client.startServer();
            List<byte[]> allTorrents = FileIO.getInstance().readTorrents();
            for (byte[] torrentBytes : allTorrents) {
                Torrent torrent = TCodec.decode(torrentBytes);
                System.out.println("Name: " + torrent.name);
            }
            System.out.println("Existing Torrents read: " + allTorrents.size());

            List<String> downloadFiles = config.getDownloadFiles();
            if (downloadFiles!=null) {
                for (String file : downloadFiles) {
                    byte[] torrentBytes = FileIO.getInstance().readTorrent(file);
                    Torrent torrent = TCodec.decode(torrentBytes);
                    client.downloadFile(torrent);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates a tracker node
     */
    private static void trackerNode(NodeConfig config) {
        try {
            Tracker tracker = new Tracker(config.getHostname(), config.getIp(), config.getPort());
            tracker.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate torrent file for a file
     *
     * @param filename  name of the file
     * @param comment   comment about the file
     * @param createdBy author
     */
    private static void generateTorrent(String filename, String comment, String createdBy) {
        try {
            byte[] data = FileIO.getInstance().readFile(filename);
            Torrent torrent = TorrentGenerator.createTorrent(filename, comment, createdBy, data);
            FileIO.getInstance().saveTorrent(torrent.getTorrentName(), TCodec.encode(torrent));
            System.out.println("Torrent generated.");
        } catch (IOException e) {
            System.out.println("File not found. Unable to generate torrent file.");
        }
    }

    /**
     * Main program. Will run one type of node
     *
     * @param args
     */
    public static void main(String[] args) {
        ApplicationConfig config = Helper.parseArgs(args);

        switch (config.getType()) {
            case Constants.TYPE_NEW_TORRENT -> generateTorrent(config.getFileName(), config.getComment(), config.getCreatedBy());
            case Constants.TYPE_CLIENT -> clientNode(config.getNodeConfig());
            case Constants.TYPE_TRACKER -> trackerNode(config.getNodeConfig());
            default -> Helper.exit("Invalid Type");
        }
    }
}
