package client;

import utils.Connection;

public class PeerHandler implements Runnable{
    private final Connection connection;

    public PeerHandler(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void run() {

    }
}
