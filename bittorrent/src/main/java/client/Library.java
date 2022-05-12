package client;

import models.Torrent;
import utils.FileIO;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
public class Library {
    Map<String, Torrent> files = new HashMap();
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Library() {
    }

    public void add(Torrent torrent) {
        lock.writeLock().lock();
        try {
            files.put(torrent.name, torrent);
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
            files.remove(torrent);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static class File extends Torrent {
        public final boolean[] downloadedPieces;

        public File(Torrent t) {
            super(t.announce,
                    t.name,
                    t.pieceLength,
                    t.piecesBlob,
                    t.pieces,
                    t.singleFileTorrent,
                    t.totalSize,
                    t.fileList,
                    t.comment,
                    t.createdBy,
                    t.creationDate,
                    t.announceList,
                    t.infoHash);

            downloadedPieces = checkDownloadedPieces();
        }

        private boolean[] checkDownloadedPieces() {
            FileIO.getInstance().read()
        }
    }
}
