package client;

import models.Torrent;
import utils.FileIO;

<<<<<<< HEAD
import java.io.IOException;
=======
>>>>>>> 8220515fbeb5de19cb94c663c95e84514fdfa3b9
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
<<<<<<< HEAD
 * <p>
 * Same information as torrent file, but additionally it has information
 * on what pieces it has downloaded
 */
public class Library {
    Map<String, File> files = new HashMap();
=======
 */
public class Library {
    Map<String, Torrent> files = new HashMap();
>>>>>>> 8220515fbeb5de19cb94c663c95e84514fdfa3b9
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Library() {
    }

    public void add(Torrent torrent) {
<<<<<<< HEAD
        File file = new File(torrent);
        lock.writeLock().lock();
        try {
            files.put(file.name, file);
=======
        lock.writeLock().lock();
        try {
            files.put(torrent.name, torrent);
>>>>>>> 8220515fbeb5de19cb94c663c95e84514fdfa3b9
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
<<<<<<< HEAD
            files.remove(torrent.name);
=======
            files.remove(torrent);
>>>>>>> 8220515fbeb5de19cb94c663c95e84514fdfa3b9
        } finally {
            lock.writeLock().unlock();
        }
    }

<<<<<<< HEAD
    static class File extends Torrent {
=======
    private static class File extends Torrent {
>>>>>>> 8220515fbeb5de19cb94c663c95e84514fdfa3b9
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

<<<<<<< HEAD
            boolean[] p;
            try {
                p = checkDownloadedPieces();
            } catch (IOException e) {
                // we don't have the file or corrupted or something
                p = null;
            }
            downloadedPieces = p;
        }

        private boolean[] checkDownloadedPieces() throws IOException {
            byte[] data = FileIO.getInstance().readFile(name);
            int numberOfPieces = (int) (totalSize / pieceLength);
            if (totalSize % pieceLength != 0)
                numberOfPieces++;

            boolean[] downloadedPieces = new boolean[numberOfPieces];
            int j = 0;
            for (int i = 0; i < totalSize; i += pieceLength) {
                if (data[i] != 0) downloadedPieces[j++] = true;
            }

            return downloadedPieces;
=======
            downloadedPieces = checkDownloadedPieces();
        }

        private boolean[] checkDownloadedPieces() {
            FileIO.getInstance().read()
>>>>>>> 8220515fbeb5de19cb94c663c95e84514fdfa3b9
        }
    }
}
