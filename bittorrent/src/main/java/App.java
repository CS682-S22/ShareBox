import client.Client;
import models.Torrent;
import tracker.Tracker;
import utils.ConnectionException;
import utils.TorrentGenerator;

import java.io.IOException;
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
            byte[] data = "This is data. just filling it with garbage value".getBytes();
            Torrent torrent = TorrentGenerator.fromFile("test.meta", "this is comment", "anchitbhatia", data);
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
        else {
            trackerNode();
        }
    }
}
