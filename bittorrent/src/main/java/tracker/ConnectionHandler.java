package tracker;

import com.google.protobuf.InvalidProtocolBufferException;
import models.Request;
import utils.Connection;

public class ConnectionHandler implements Runnable {
    private final Connection connection;

    public ConnectionHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        while (!this.connection.isClosed()) {
            byte[] message = this.connection.receive();
            if (message!=null) {
                try {
                    Request.RequestPacket packet = Request.RequestPacket.parseFrom(message);
                } catch (InvalidProtocolBufferException e) {
                    System.out.println("Invalid packet received");
                    e.printStackTrace();
                }
            }
        }

    }
}
