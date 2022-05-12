package tracker;

import com.google.protobuf.InvalidProtocolBufferException;
import protos.Proto;
import protos.Proto.Request;
import protos.Proto.Request.RequestType;
import utils.Connection;

public class ConnectionHandler implements Runnable {
    private final Connection connection;

    public ConnectionHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        while (!this.connection.isClosed()) {
            byte[] message = connection.receive();
            if (message == null) return;
            Proto.Request proto;
            RequestType requestType;
            try {
                proto = Request.parseFrom(message);
                requestType = proto.getRequestType();
            } catch (InvalidProtocolBufferException e) {
                System.out.println("Invalid packet received");
                e.printStackTrace();
                return;
            }

            if (requestType.equals(RequestType.PEER_MEMBERSHIP)) {
                for (Proto.Torrent torrent : proto.getTorrentsList())
                    System.out.println(torrent.getFilename());
            }
        }

    }
}
