package tracker;

import com.google.protobuf.InvalidProtocolBufferException;
import protos.Proto;
import utils.Connection;

public class ConnectionHandler implements Runnable {
    private final Connection connection;

    public ConnectionHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        byte[] data = connection.receive();
        try {
            Proto.Request proto = Proto.Request.parseFrom(data);
            Proto.Request.RequestType requestType = proto.getRequestType();

            if (requestType.equals(Proto.Request.RequestType.PEER_MEMBERSHIP)) {
                for (Proto.Torrent torrent : proto.getTorrentsList())
                    System.out.println(torrent.getFilename());
            }
        } catch (InvalidProtocolBufferException e) {
            connection.close();
            return;
        }
    }
}
