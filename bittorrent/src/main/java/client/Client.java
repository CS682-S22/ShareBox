package client;

import com.google.protobuf.InvalidProtocolBufferException;
import models.Torrent;
import protos.Proto;
import protos.Response;
import utils.Connection;
import utils.ConnectionException;
import utils.Helper;
import utils.Node;

import java.io.IOException;
import java.net.Socket;
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

    public Client(String hostname, String ip, int port) throws IOException {
        super(hostname, ip, port);
        initializeServer(new PeerServer());
        library = initLibrary();
        trackerConnection = null;
    }

    @Override
    public void startServer() {
        super.startServer();
        try {
            trackerConnection = ClientInit.joinSwarm(hostname, ip, port);
        } catch (ConnectionException ignored) {
            // ignore for the time being
        }
    }

    private Map<Long, Response.PeersList> getPiecesInformation(String fileName) throws ConnectionException {
        Proto.Request request = Proto.Request.newBuilder().
                setNode(Helper.getNodeDetailsObject(this)).
                setRequestType(Proto.Request.RequestType.REQUEST_PEERS).
                setFileName(fileName)
                .build();
        trackerConnection.send(request.toByteArray());
        byte[] response = trackerConnection.receive();
        try {
            Response.FileInfo fileInfo = Response.FileInfo.parseFrom(response);
            return fileInfo.getPiecesInfoMap();
        } catch (InvalidProtocolBufferException e) {
            return null;
        }
    }

    public void downloadFile(Torrent torrent) throws ConnectionException {
        String fileName = torrent.getName();
        System.out.println("Downloading " + fileName);
        Map<Long, Response.PeersList> piecesInfo = getPiecesInformation(fileName);
        System.out.println("Response: " + piecesInfo);
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
