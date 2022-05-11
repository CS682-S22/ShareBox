package tracker;

import utils.Connection;

public class ClientHandler implements Runnable{
    private final Connection connection;

    public ClientHandler(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        System.out.println("Handling client");
    }
}
