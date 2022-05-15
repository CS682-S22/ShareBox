package client;

import models.Torrent;
import utils.FileIO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 * <p>
 * Same information as torrent file, but additionally it has information
 * on what pieces it has downloaded
 */
public class Library {
    Map<String, TorrentDetails> files = new HashMap<>();
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Library() {
    }

    public TorrentDetails add(Torrent torrent) {
        TorrentDetails torrentDetails = new TorrentDetails(torrent);
        lock.writeLock().lock();
        try {
            files.put(torrentDetails.name, torrentDetails);
            return torrentDetails;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void remove(String filename) {
        lock.writeLock().lock();
        try {
            files.remove(filename);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void remove(Torrent torrent) {
        lock.writeLock().lock();
        try {
            files.remove(torrent.name);
        } finally {
            lock.writeLock().unlock();
        }
    }

    static class TorrentDetails extends Torrent {
        public final Map<Long, Boolean> downloadedPieces;

        public TorrentDetails(Torrent t) {
            super(t.announce,
                    t.name,
                    t.pieceLength,
                    t.pieces,
                    t.singleFileTorrent,
                    t.totalSize,
                    t.fileList,
                    t.comment,
                    t.createdBy,
                    t.creationDate,
                    t.announceList,
                    t.infoHash);

            Map<Long, Boolean> p;
            try {
                p = checkDownloadedPieces();
            } catch (IOException e) {
                // we don't have the file or corrupted or something
                p = null;
            }
            downloadedPieces = p;
        }

        private Map<Long, Boolean> checkDownloadedPieces() throws IOException {
            byte[] data = FileIO.getInstance().readFile(name);
            int numberOfPieces = (int) (totalSize / pieceLength);
            if (totalSize % pieceLength != 0)
                numberOfPieces++;

            Map<Long, Boolean> downloadedPieces = new HashMap<>();
            int j = 0;
            for (int i = 0; i < totalSize; i += pieceLength) {
                if (data[i] != 0) downloadedPieces.put((long) i, true);
            }

            return downloadedPieces;
        }
    }
}
