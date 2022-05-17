package client;

import protos.Node;
import protos.Proto;
import utils.Connection;
import utils.ConnectionException;
import utils.HeartbeatScheduler;

import static utils.Constants.HEARTBEAT_INTERVAL_MS;

/**
 * @author Alberto Delgado on 5/15/22
 * @author anchit bhatia
 * @project bittorrent
 * <p>
 * Handles heartbeat to tracker
 */
class HeartbeatManager {
    private HeartbeatScheduler heartbeatScheduler;
    private final String hostname;
    private final String ip;
    private final int port;

    HeartbeatManager(String hostname, String ip, int port) {
        this.hostname = hostname;
        this.ip = ip;
        this.port = port;
    }

    public void init(Connection to) {
        Heartbeat heartbeat = new Heartbeat(to);
        heartbeatScheduler = new HeartbeatScheduler(heartbeat, HEARTBEAT_INTERVAL_MS);
        heartbeatScheduler.start();
    }

    public void stop() {
        heartbeatScheduler.cancel();
    }

    /**
     * Heartbeat to be sent periodically
     */
    private class Heartbeat implements Runnable {
        private final Connection conn;

        public Heartbeat(Connection conn) {
            this.conn = conn;
        }

        @Override
        public void run() {
            Proto.Request hb = Proto.Request.newBuilder()
                    .setRequestType(Proto.Request.RequestType.PEER_HEARTBEAT)
                    .setNode(
                            Node.NodeDetails.newBuilder()
                                    .setHostname(hostname)
                                    .setIp(ip)
                                    .setPort(port)
                                    .build()
                    )
                    .build();

            try {
                conn.send(hb.toByteArray());
            } catch (ConnectionException e) {
                System.err.println("Could not send heartbeat to tracker.");
            }
        }
    }
}

