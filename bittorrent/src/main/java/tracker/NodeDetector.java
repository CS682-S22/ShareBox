package tracker;

import protos.Node;
import utils.HeartbeatScheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static utils.Constants.HEARTBEAT_INTERVAL_MS;

/**
 * @author Alberto Delgado on 5/15/22
 * @project bittorrent
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

    void heartbeatReceived(Node.NodeDetails node) {
        receivedTimes.put(node.getIp(), System.nanoTime());
    }

    private class HeartbeatCheck implements Runnable {

        @Override
        public void run() {
            for (Map.Entry<String, Long> entry : receivedTimes.entrySet()) {
                long currentTime = System.nanoTime();
                long delay = currentTime - entry.getValue();
                if (delay > 3 * HEARTBEAT_INTERVAL_MS)
                    db.removePeer(entry.getKey());
            }
        }
    }
}
