package utils;

import protos.Node.NodeDetails;

import java.net.InetSocketAddress;
import java.net.Socket;

public class Helper {

    public static NodeDetails getNodeDetailsObject(Node node) {
        return NodeDetails.newBuilder().
                setHostname(node.getHostname()).
                setPort(node.getPort()).
                setIp(node.getIp()).
                build();
    }

    public static Node getNodeObject(NodeDetails nodeDetails) {
        return new Node(nodeDetails.getHostname(), nodeDetails.getIp(), nodeDetails.getPort());
    }

    public static String getIpFromSocket(Socket socket) {
        return (((InetSocketAddress) socket.getRemoteSocketAddress())
                .getAddress())
                .toString()
                .replace("/", "");
    }
}
