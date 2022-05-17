package client;

import models.Torrent;

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
    Map<String, Torrent> files = new HashMap<>();
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Library() {
    }

    public Torrent add(Torrent torrent) {
        lock.writeLock().lock();
        try {
            torrent.checkDownloadedPieces();
            files.put(torrent.name, torrent);
        } catch (IOException ignored) {
            // if no file exists means it has no pieces
        } finally {
            lock.writeLock().unlock();
        }
        return torrent;
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
        }
    }
}