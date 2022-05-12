package client;

import utils.Connection;

public class ConnectionHandler implements Runnable{
    private final Connection connection;

    public ConnectionHandler(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void run() {

    }
}
