package client;

import com.google.protobuf.InvalidProtocolBufferException;
import models.Torrent;
import protos.Proto;
import protos.Response;
import protos.Response.PeersList;
import utils.ConnectionException;
import utils.FileIO;
import utils.Helper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * @author Alberto Delgado on 5/16/22
 * @project bittorrent
 */
public class FileDownloader implements Runnable {
    private final FileIO fileIO = FileIO.getInstance();
    private final Client client;
    private final Torrent torrent;
    private final PieceDownloader pieceDownloader;
    private Map<Long, PeersList> peers;
    private boolean isDone = false;
    private boolean testing = false;

    public FileDownloader(Client client, Torrent torrent) {
        this.client = client;
        this.torrent = torrent;
        this.pieceDownloader = new PieceDownloader();
    }

    public Map<Long, byte[]> download() throws ConnectionException, IOException, ExecutionException, InterruptedException {
        String filename = torrent.name;
        System.out.println("===============================================");
        System.out.println("    Initiating download for " + torrent.name);
        System.out.println("===============================================");


        Map<Long, PeersList> piecesInfo = getPiecesInformation(filename);

        if (piecesInfo == null) {
            System.out.println("No response from Tracker");
            return null;
        } else if (piecesInfo.size() == 0) {
            System.out.println("No seeder currently seeding " + filename);
            return null;
        } else if (piecesInfo.containsKey(-1L) && piecesInfo.size() == 1) {
            System.out.println("No seeders currently seeding " + filename);
            return null;
        }

        System.out.println("Response: " + piecesInfo);
        Set<Map.Entry<Long, PeersList>> piecesEntrySets = piecesInfo.entrySet();

        // do not download already downloaded pieces.
        // this algo has plenty of room for improvement.
        Set<Map.Entry<Long, PeersList>> missingEntries = new HashSet<>();
        for (Map.Entry<Long, PeersList> entry : piecesEntrySets) {
            if (!torrent.hasPiece(entry.getKey()))
                missingEntries.add(entry);
        }

        // sorted pieces by rarest first
        List<Map.Entry<Long, PeersList>> rarestFirst = new ArrayList<>(missingEntries);
        rarestFirst.sort((a, b) -> {
            int sizeA = a.getValue().getSerializedSize();
            int sizeB = b.getValue().getSerializedSize();
            return sizeA - sizeB;
        });

        Map<Long, byte[]> data = new HashMap<>();
        while (!rarestFirst.isEmpty()) {
            Map<Long, byte[]> downloaded = pieceDownloader.downloadPieces(client, torrent, rarestFirst);
            data.putAll(download());

            // remove downloaded pieces from rarest first list
            // update torrent information
            for (Map.Entry<Long, byte[]> piece : downloaded.entrySet()) {
                for (Map.Entry<Long, PeersList> item : rarestFirst)
                    if (Objects.equals(item.getKey(), piece.getKey())) {
                        rarestFirst.remove(item);
                        torrent.addDownloadedPiece(piece.getKey());
                    }
            }
        }

        isDone = true;
        return data;
    }

    private byte[] createBlobArray(Map<Long, byte[]> data) {
        if (data == null) return null;
        List<Map.Entry<Long, byte[]>> sortedData = new ArrayList<>(data.entrySet());
        sortedData.sort((a, b) -> (int) (a.getKey() - b.getKey()));
        byte[] file = new byte[Math.toIntExact(torrent.totalSize)];

        int j = 0;
        for (Map.Entry<Long, byte[]> item : sortedData) {
            for (int i = 0; i < item.getValue().length; i++) {
                file[j++] = item.getValue()[i];
            }
        }

        return file;
    }

    private Map<Long, PeersList> getPiecesInformation(String fileName) throws ConnectionException {
        Proto.Request request = Proto.Request.newBuilder().
                setNode(Helper.getNodeDetailsObject(client)).
                setRequestType(Proto.Request.RequestType.REQUEST_PEERS).
                setFileName(fileName)
                .build();

        client.trackerConnection.send(request.toByteArray());
        byte[] response = client.trackerConnection.receive();
        try {
            Response.FileInfo fileInfo = Response.FileInfo.parseFrom(response);
            return fileInfo.getPiecesInfoMap();
        } catch (InvalidProtocolBufferException e) {
            return null;
        }
    }

    public boolean isDone() {
        return isDone;
    }

    @Override
    public void run() {
        try {
            byte[] file = createBlobArray(download());
            if (file == null) {
                System.out.println("Something went wrong downloading file " + torrent.name);
                return;
            }
            if (!testing)
                fileIO.saveFile(torrent.name, file);
            else
                fileIO.saveFile(torrent.name + "-test", file);
        } catch (ConnectionException | IOException | InterruptedException | ExecutionException ignored) {
            // ignored
        }
    }

    public void testing() {
        testing = true;
        fileIO.testing();
    }
}
