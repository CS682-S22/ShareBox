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
 * @author anchit bhatia
 * @project bittorrent
 * <p>
 * Logic to download a file from other peers. It will ask the tracker initially
 * for the information of what peers own what pieces. After that it will apply
 * rarest first to retrieve the rarest pieces first.
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

    /**
     * Downloads the file from remote peers using rarest first
     *
     * @return
     * @throws ConnectionException
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private Map<Long, byte[]> download() throws ConnectionException, IOException, ExecutionException, InterruptedException {
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


        while (!rarestFirst.isEmpty()) {
            Map<Long, byte[]> downloaded = pieceDownloader.downloadPieces(client, torrent, rarestFirst);
            torrent.piecesCache.putAll(downloaded);

            // remove downloaded pieces from rarest first list
            // update torrent information
            for (Map.Entry<Long, byte[]> piece : downloaded.entrySet()) {
                System.out.println("I have " + piece.getKey());
                for (Map.Entry<Long, PeersList> item : rarestFirst)
                    if (Objects.equals(item.getKey(), piece.getKey())) {
                        System.out.println("Removing from rarest first " + piece.getKey());
                        rarestFirst.remove(item);
                        torrent.addDownloadedPiece(piece.getKey());
                        break;
                    }
            }
            client.notifyTracker(torrent, new ArrayList<>(downloaded.keySet()));
        }

        System.out.println("Torrent size: " + torrent.totalSize);
        isDone = true;
        return torrent.piecesCache;
    }

    /**
     * Sorts the downloaded pieces into an array
     *
     * @param data
     * @return
     */
    private byte[] createBlobArray(Map<Long, byte[]> data) {
        if (data == null) return null;
        List<Map.Entry<Long, byte[]>> sortedData = new ArrayList<>(data.entrySet());
        sortedData.sort((a, b) -> (int) (a.getKey() - b.getKey()));
        byte[] file = new byte[Math.toIntExact(torrent.totalSize)];

        int j = 0;
        for (Map.Entry<Long, byte[]> item : sortedData) {
            int bytesLength = item.getValue().length;
            int bytesToRead = Math.min((int) (torrent.totalSize - j), bytesLength);
            for (int i = 0; i < bytesToRead; i++) {
                file[j] = item.getValue()[i];
                j++;
            }
        }

        return file;
    }

    /**
     * Retrieves the information of a file: what peers have what pieces
     *
     * @param fileName
     * @return
     * @throws ConnectionException
     */
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

    /**
     * Runs main logic:
     * - Download pieces
     * - Sort pieces
     * - Persist file locally
     */
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
//            torrent.piecesCache.clear();
        } catch (ConnectionException | IOException | InterruptedException | ExecutionException ignored) {
            // ignored
        }
        System.out.println("\nFile downloaded successfully!");
    }

    /**
     * Sets the FileDownloader to testing mode
     */
    public void testing() {
        testing = true;
        fileIO.testing();
    }
}
