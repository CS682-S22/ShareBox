package utils;

import models.Torrent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
public class TorrentGenerator {
    public static Torrent fromFile(
            String filename,
            String comment,
            String createdBy,
            byte[] data) {
        Torrent torrent = createTorrent(filename, comment, createdBy, data);
        String torrentName = Helper.getTorrentName(filename);
        FileIO.getInstance().saveTorrent(torrentName, TCodec.encode(torrent));
        return torrent;
    }

    public static Torrent createTorrent(
            String filename,
            String comment,
            String createdBy,
            byte[] data) {
        // try to make each piece %5  of the file
        // or a max size defined in globals.
        long totalSize = data.length;
        long pieceLength = getPieceLength(data);
        String hash = Arrays.toString(Encryption.encodeSHA256(data));
        List<String> pieces = getPieces(data, totalSize, pieceLength);
        boolean singleFileTorrent = true;
        List<Torrent.TorrentFile> fileList = null;
        Date creationDate = new Date(System.currentTimeMillis());
        List<String> announceList = null;
        return new Torrent(
                Globals.trackerIP + Globals.trackerPort,
                filename,
                pieceLength,
                pieces,
                singleFileTorrent,
                totalSize,
                fileList,
                comment,
                createdBy,
                creationDate,
                announceList,
                hash
        );
    }

    static Long getPieceLength(byte[] data) {
        long pieceLength = (long) (data.length * 0.05);
        return Math.min(pieceLength, Globals.PIECE_LENGTH_MAX);
    }

    static List<String> getPieces(byte[] data, long totalSize, long pieceLength) {
        List<String> pieces = new ArrayList<>();
        for (int i = 0; i < totalSize; i += pieceLength) {
            int z = 0;
            byte[] piece = new byte[(int) pieceLength];
            for (int j = i; j < i + pieceLength; j++) {
                if (i + z == totalSize) break;
                piece[z++] = data[j];
            }

            byte[] sha = Encryption.encodeSHA1(piece);
            pieces.add(Arrays.toString(sha));
        }
        return pieces;
    }
}
