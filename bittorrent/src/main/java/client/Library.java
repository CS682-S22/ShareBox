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
 * Contains information of all the local files.
 */
public class Library {
    Map<String, Torrent> files = new HashMap<>();
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Library() {
    }

    /**
     * Torrent getter
     *
     * @param fileName
     * @return
     */
    public Torrent getTorrent(String fileName) {
        return this.files.getOrDefault(fileName, null);
    }

    /**
     * Torrent setter
     *
     * @param torrent
     * @return
     */
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

    /**
     * Torrent remove
     *
     * @param filename
     */
    public void remove(String filename) {
        lock.writeLock().lock();
        try {
            files.remove(filename);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Torrent remove
     *
     * @param torrent
     */
    public void remove(Torrent torrent) {
        lock.writeLock().lock();
        try {
            files.remove(torrent.name);
        } finally {
            lock.writeLock().unlock();
        }
    }
}