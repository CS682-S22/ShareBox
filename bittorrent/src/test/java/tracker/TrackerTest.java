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
    private static final String peerName = "peer1";
    private static final String peerIP = "127.0.0.1";
    private static final int peerPort = 6001;


    static {
        try {
            client = new Client(
                    peerName,
                    peerIP,
                    peerPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            tracker = new Tracker(
                    Globals.trackerName,
                    Globals.trackerIP,
                    Globals.trackerPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void membership() throws InterruptedException {
        tracker.startServer();
        client.startServer();

        Thread.sleep(500);

        Map<String, Node> peerList = tracker.swarmDatabase.getPeersList();
        Map<String, Constants.Status> peerStatus = tracker.swarmDatabase.getPeerStatus();
        assertEquals(1, peerList.size());

        Node swarmPeer = peerList.get("127.0.0.1");
        // tracker has node information
        assertEquals(client.getHostname(), swarmPeer.getHostname());
        assertEquals(client.getPort(), swarmPeer.getPort());
        assertEquals(client.getIp(), swarmPeer.getIp());
        // tracker has node as ONLINE
        assertEquals(peerStatus.get("127.0.0.1"), Constants.Status.ONLINE);
    }

    @Test
    void peerData() {
    }

    @Test
    void start() throws IOException, InterruptedException {
        Tracker tracker = new Tracker(Globals.trackerName, Globals.trackerIP, Globals.trackerPort);
        tracker.startServer();
        Client peer = new Client(peerName, peerIP, peerPort);
        peer.startServer();

        Thread.sleep(500);
    }

    @Test
    void heartbeat() throws IOException, InterruptedException {
        tracker.startServer();
        client.startServer();
        Map<String, Node> peerList = tracker.getPeerList();

        // should have peer as ONLINE
        Thread.sleep(5001);
        assertEquals(1, peerList.size());
        assertEquals(tracker.getPeerStatus().get(peerIP), Constants.Status.ONLINE);

        // should have peer as OFFLINE
        client.stopServer();
        Thread.sleep(20002);
        assertEquals(0, peerList.size());
        assertEquals(tracker.getPeerStatus().get(peerIP), Constants.Status.OFFLINE);
    }
}