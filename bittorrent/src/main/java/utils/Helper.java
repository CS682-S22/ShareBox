package utils;

import protos.Node.NodeDetails;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Helper methods
 *
 * @author alberto delgado
 * @author anchit bhatia
 */
public class Helper {

    /**
     * Retrieves NodeDetails from Node
     *
     * @param node
     * @return
     */
    public static NodeDetails getNodeDetailsObject(Node node) {
        return NodeDetails.newBuilder().
                setHostname(node.getHostname()).
                setPort(node.getPort()).
                setIp(node.getIp()).
                build();
    }

    /**
     * Retrieves Node from NodeDetails
     *
     * @param nodeDetails
     * @return
     */
    public static Node getNodeObject(NodeDetails nodeDetails) {
        return new Node(nodeDetails.getHostname(), nodeDetails.getIp(), nodeDetails.getPort());
    }

    /**
     * Gets IP from a socket
     *
     * @param socket
     * @return
     */
    public static String getIpFromSocket(Socket socket) {
        return (((InetSocketAddress) socket.getRemoteSocketAddress())
                .getAddress())
                .toString()
                .replace("/", "");
    }

    /**
     * Creates a torrent name from a given file name
     *
     * @param filename
     * @return
     */
    public static String getTorrentName(String filename) {
        int i = filename.indexOf('.');
        if (i > 0)
            filename = filename.substring(0, i);

        return filename + Constants.TORRENT_EXT;
    }

    /**
     * Creates a Peer ID from a node
     *
     * @param node
     * @return
     */
    public static String getPeerId(Node node) {
        return node.getIp() + ":" + node.getPort();
    }

}
