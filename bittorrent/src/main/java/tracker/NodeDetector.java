package tracker;

import protos.Node;
import utils.HeartbeatScheduler;
import utils.Helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static utils.Constants.HEARTBEAT_INTERVAL_MS;

/**
 * @author Alberto Delgado on 5/15/22
 * @author anchit bhatia
 * @project bittorrent
 * <p>
 * Detects if a node has gone offline
 */
public class NodeDetector {
    final SwarmDatabase db;
    final HeartbeatScheduler heartbeatScheduler;
    final Map<String, Long> receivedTimes = new ConcurrentHashMap<>();

    NodeDetector(SwarmDatabase db) {
        this.db = db;
        HeartbeatCheck heartbeatCheck = new HeartbeatCheck();
        this.heartbeatScheduler = new HeartbeatScheduler(heartbeatCheck, HEARTBEAT_INTERVAL_MS).start();
    }

    /**
     * Updates received heartbeat
     *
     * @param node
     */
    void heartbeatReceived(Node.NodeDetails node) {
        receivedTimes.put(Helper.getPeerId(Helper.getNodeObject(node)), System.nanoTime());
    }

    /**
     * Checks if peer has timed out
     */
    private class HeartbeatCheck implements Runnable {
        private final int msToNano = 1000000;

        @Override
        public void run() {
            for (Map.Entry<String, Long> entry : receivedTimes.entrySet()) {
                long currentTime = System.nanoTime();
                long delay = currentTime - entry.getValue();
                if (delay > (3 * HEARTBEAT_INTERVAL_MS * msToNano))
                    db.removePeer(entry.getKey());
            }
        }
    }
}
