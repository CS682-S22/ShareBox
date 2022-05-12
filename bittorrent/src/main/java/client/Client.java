package client;

import utils.Connection;
import utils.ConnectionException;
import utils.Node;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static client.ClientInit.initLibrary;

/**
 * @author Alberto Delgado on 5/9/22
 * @project dsd-final-project-anchitbhatia
 */
public class Client extends Node {
    Library library;

    public Client(String hostname, String ip, int port) throws IOException {
        super(hostname, ip, port);
        initializeServer(new PeerServer());
        library = initLibrary();
    }

    private class PeerServer implements Runnable {
        private final ExecutorService peerConnectionPool;

        public PeerServer() {
            this.peerConnectionPool = Executors.newCachedThreadPool();
        }

        @Override
        public void run() {
            try {
                ClientInit.joinSwarm();
            } catch (ConnectionException ignored) {
                // ignore for the time being
            }

            try {
                while (isServerRunning) {
                    Socket clientSocket = serverSocket.accept();
                    Connection connection = new Connection(clientSocket);
                    this.peerConnectionPool.execute(new ConnectionHandler(connection));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
