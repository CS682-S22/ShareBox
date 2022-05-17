import client.Client;
import models.Torrent;
import tracker.Tracker;
import utils.ConnectionException;
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
                System.out.println("Name: " + torrent.getName());
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
                System.out.println("Name: " + torrent.getName());
            }

            byte[] data = FileIO.getInstance().readFile(fileName);
            Torrent torrent = TorrentGenerator.createTorrent(fileName, "Test", "anchitbhatia", data);
//            byte[] data = "This is data. just filling it with garbage value".getBytes();
//            Torrent torrent = TorrentGenerator.fromFile("test.meta", "this is comment", "anchitbhatia", data);

//            client.sendTorrentInfo(torrent);
            client.downloadFile(torrent);
        } catch (IOException | ConnectionException e) {
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
    public static void main(String[] args) {
        if (Objects.equals(args[1], "--client")) {
            clientNode();
        }
        else if (Objects.equals(args[1], "--client2")) {
            clientNode(args[2]);
        }
        else {
            trackerNode();
        }
    }
}
