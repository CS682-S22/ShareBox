package tracker;

import client.Client;
import org.junit.jupiter.api.Test;
import utils.Constants;
import utils.Globals;
import utils.Node;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alberto Delgado on 5/12/22
 * @project bittorrent
 */
class TrackerTest {
    private static final Tracker tracker;
    private static final Client client;

    static {
        try {
            client = new Client(
                    "Peer_1",
                    "127.0.0.1",
                    5001);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            tracker = new Tracker(
                    "Tracker",
                    Globals.trackerIP,
                    Globals.trackerPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void membership() throws IOException, InterruptedException {
        tracker.startServer();
        client.startServer();

        Thread.sleep(500);

        Map<String, Node> peerList = tracker.swarmDatabase.getPeersList();
        Map<String, Constants.status> peerStatus = tracker.swarmDatabase.getPeerStatus();
        assertEquals(1, peerList.size());

        Node swarmPeer = peerList.get("127.0.0.1");
        // tracker has node information
        assertEquals(client.getHostname(), swarmPeer.getHostname());
        assertEquals(client.getPort(), swarmPeer.getPort());
        assertEquals(client.getIp(), swarmPeer.getIp());
        // tracker has node as ONLINE
        assertEquals(peerStatus.get("127.0.0.1"), Constants.status.ONLINE);
    }

    @Test
    void peerData() {
    }
}