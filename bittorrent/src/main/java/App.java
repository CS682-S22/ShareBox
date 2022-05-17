import client.Client;
import models.Torrent;
import tracker.Tracker;
import utils.*;

import java.io.IOException;
import java.util.List;

/**
 * @author Alberto Delgado on 5/9/22
 * @author Anchit Bhatia
 * @project dsd-final-project-anchitbhatia
 */
public class App {
    private static void clientNode(NodeConfig config) {
        try {
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

    private static void trackerNode(NodeConfig config) {
        try {
            Tracker tracker = new Tracker(config.getHostname(), config.getIp(), config.getPort());
            tracker.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
