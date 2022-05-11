package utils;

import java.net.InetSocketAddress;
import java.net.Socket;

public class Helper {

    public static String getIpFromSocket(Socket socket) {
        return (((InetSocketAddress) socket.getRemoteSocketAddress())
                .getAddress())
                .toString()
                .replace("/", "");
    }
}
