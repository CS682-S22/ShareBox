package client;

import org.junit.jupiter.api.Test;
import tracker.Tracker;
import utils.ConnectionException;
import utils.Globals;

import java.io.IOException;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
class ClientTest {

    @Test
    void start() throws IOException, ConnectionException, InterruptedException {
        Tracker tracker = new Tracker("Tracker", Globals.trackerIP, Globals.trackerPort);
        tracker.startServer();
        Client peer = new Client("Peer1", "127.0.0.1", 5001);
        peer.startServer();

        Thread.sleep(500);
    }
}