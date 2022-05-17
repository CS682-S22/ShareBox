package client;

import models.Torrent;
import utils.Connection;
import utils.ConnectionException;
import utils.Node;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static client.ClientInit.initLibrary;

/**
 * @author Alberto Delgado on 5/9/22
 * @project dsd-final-project-anchitbhatia
 */
public class Client extends Node {
    Library library;
    Connection trackerConnection;
    HeartbeatManager heartbeatManager;
    Map<String, FileDownloader> downloads = new HashMap<>();
    boolean testing = false;

    public Client(String hostname, String ip, int port) throws IOException {
        super(hostname, ip, port);
        initializeServer(new PeerServer());
        library = initLibrary();
    }

    @Override
    public void startServer() {
        super.startServer();
        try {
            trackerConnection = ClientInit.joinSwarm(hostname, ip, port);
            heartbeatManager = new HeartbeatManager(hostname, ip, port);
            heartbeatManager.init(trackerConnection);
        } catch (ConnectionException ignored) {
            // ignore for the time being
        }
    }

    @Override
    public void stopServer() {
        super.stopServer();
        heartbeatManager.stop();
    }

    public void downloadFile(Torrent torrent) {
        FileDownloader downloader = new FileDownloader(this, torrent);
        if (testing) downloader.testing();
        downloads.put(torrent.name, downloader);
        new Thread(downloader).start();
    }

    public Client testing() {
        testing = true;
<<<<<<< HEAD
        library = initLibrary();
=======
>>>>>>> 5a1cd77373323da5af9b5af2e85e871aaa9b461f
        return this;
    }

    private class PeerServer implements Runnable {
        private final ExecutorService peerConnectionPool;

        public PeerServer() {
            this.peerConnectionPool = Executors.newCachedThreadPool();
        }

        @Override
        public void run() {
            try {
                System.out.println("Starting server on clientNode at " + port);
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
