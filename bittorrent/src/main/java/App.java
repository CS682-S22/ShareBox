import client.Client;
import models.Torrent;
import tracker.Tracker;
import utils.FileIO;
import utils.TCodec;
import utils.TorrentGenerator;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author Alberto Delgado on 5/9/22
 * @project dsd-final-project-anchitbhatia
 */
public class App {
    private static void clientNode() {
        try {
            Client client = new Client("localhost", "127.0.0.1", 5001);
            client.startServer();
            List<byte[]> allTorrents = FileIO.getInstance().readTorrents();
            System.out.println("Torrents read: " + allTorrents.size());
            for (byte[] torrentBytes : allTorrents) {
                Torrent torrent = TCodec.decode(torrentBytes);
                System.out.println("Name: " + torrent.name);
            }
//            byte[] data = "This is data. just filling it with garbage value".getBytes();
//            Torrent torrent = TorrentGenerator.fromFile("test.meta", "this is comment", "anchitbhatia", data);

//            client.sendTorrentInfo(torrent);
//            client.downloadFile(torrent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void clientNode(String fileName) {
        try {
            Client client = new Client("localhost", "127.0.0.1", 5002);
            client.startServer();
            List<byte[]> allTorrents = FileIO.getInstance().readTorrents();
            System.out.println("Torrents read: " + allTorrents.size());
            for (byte[] torrentBytes : allTorrents) {
                Torrent torrent = TCodec.decode(torrentBytes);
                System.out.println("Name: " + torrent.name);
            }

            byte[] data = FileIO.getInstance().readFile(fileName);
            Torrent torrent = TorrentGenerator.createTorrent(fileName, "Test", "anchitbhatia", data);
//            byte[] data = "This is data. just filling it with garbage value".getBytes();
//            Torrent torrent = TorrentGenerator.fromFile("test.meta", "this is comment", "anchitbhatia", data);

//            client.sendTorrentInfo(torrent);
            client.downloadFile(torrent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void trackerNode() {
        try {
            Tracker tracker = new Tracker("localhost", "127.0.0.1", 5000);
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
        if (args.length == 0 || args.length > 4) {
            System.out.println("What do you want to run?");
            System.out.println("Use --client to run a client");
            System.out.println("Use --tracker to run a tracker server");
            System.out.println("Use --new-torrent <filename> <comment> <createdBy> to generate a new torrent file");
        } else if (Objects.equals(args[0], "--client")) {
            clientNode();
        } else if (Objects.equals(args[0], "--client2")) {
            clientNode(args[1]);
        } else if (Objects.equals(args[0], "--new-torrent")) {
            generateTorrent(args[1], args[2], args[3]);
        } else if (Objects.equals(args[0], "--tracker")) {
            trackerNode();
        }
    }
}
