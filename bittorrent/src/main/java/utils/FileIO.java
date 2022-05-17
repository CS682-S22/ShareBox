package utils;

import models.Torrent;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 * <p>
 * FS. Class to handle writing and reading files.
 * <p>
 * Has additionally testing method to persist to testing folders.
 */
public class FileIO {
    private static final String TEST_DIR = "./src/test/java/utils";
    private static final String TORRENTS_FINAL_DIR = "./torrents/";
    private static final String TORRENTS_TEST_DIR = TEST_DIR + "/tests_torrents/";
    private static final String LIBRARY_DIR = "./library/";
    private static final String LIBRARY_TEST_DIR = TEST_DIR + "/tests_library/";
    private static String DIR = LIBRARY_DIR;
    private static String TORRENTS_DIR = TORRENTS_FINAL_DIR;
    private static File folderDir;
    private static File torrentsDir;

    private FileIO() {
    }

    /**
     * Ensures folders are existing before reading/writing
     *
     * @return
     */
    public static synchronized FileIO getInstance() {
        // Make sure library folder exists.
        File libraryDir = new File(LIBRARY_DIR);
        if (!libraryDir.exists()) {
            libraryDir.mkdir();
        }

        torrentsDir = new File(TORRENTS_DIR);
        if (!torrentsDir.exists()) {
            torrentsDir.mkdir();
        }

        folderDir = libraryDir;
        return Holder.INSTANCE;
    }

    /**
     * Saves a file to library folder
     *
     * @param filename
     * @param data
     * @return
     */
    public boolean saveFile(String filename, byte[] data) {
        return write(DIR, filename, data);
    }

    /**
     * Saves torrent to torrent folder
     *
     * @param filename
     * @param data
     * @return
     */
    public boolean saveTorrent(String filename, byte[] data) {
        return write(TORRENTS_DIR, filename, data);
    }

    /**
     * Write helper method
     *
     * @param dir
     * @param filename
     * @param data
     * @return
     */
    private boolean write(String dir, String filename, byte[] data) {
        try (FileOutputStream writer = new FileOutputStream(dir + filename)) {
            writer.write(data);
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Reads file from library folder
     *
     * @param torrent
     * @return
     */
    public Map<Long, byte[]> readFile(Torrent torrent) {
        String filename = torrent.name;
        long pieceLength = torrent.pieceLength;
        Map<Long, byte[]> pieces = new HashMap<>();
        System.out.println("Cache before: " + torrent.piecesCache.size());
        try {
            InputStream inputStream = new FileInputStream(DIR + filename);
            boolean running = true;
            long i = 0L;
            while (running) {
                byte[] bytes = new byte[Math.toIntExact(pieceLength)];
                int bytesRead = inputStream.read(bytes, 0, Math.toIntExact(pieceLength));
                if (bytesRead != -1) {
                    torrent.piecesCache.put(i, bytes);
                    torrent.pieces.put(i, Arrays.toString(Encryption.encodeSHA256(bytes)));
                    i++;
                } else {
                    running = false;
                }
            }
            System.out.println("Cache after: " + torrent.piecesCache.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pieces;
    }

    /**
     * Reads a piece instead of entire file
     *
     * @param torrent
     * @param pieceNumber
     * @return
     */
    public byte[] readPiece(Torrent torrent, long pieceNumber) {
        String filename = torrent.name;
        long pieceLength = torrent.pieceLength;
        System.out.println("Piece requested, fileName: " + filename + ", pieceNumber: " + pieceNumber + ", pieceLength: " + pieceLength);
        byte[] piece = torrent.piecesCache.getOrDefault(pieceNumber, null);
        if (piece == null) {
            System.out.println("Does not contain piece");
            readFile(torrent);
        }
        piece = torrent.piecesCache.get(pieceNumber);
        return piece;
    }

    /**
     * Reads all files from library
     *
     * @return
     * @throws IOException
     */
    public List<byte[]> readFilesInLibrary() throws IOException {
        return readFilesFromDir(folderDir, DIR);
    }

    /**
     * Reads all torrents from torrents folder
     *
     * @return
     * @throws IOException
     */
    public List<byte[]> readTorrents() throws IOException {
        return readFilesFromDir(torrentsDir, TORRENTS_DIR);
    }

    /**
     * Helper method to read files from a dir
     *
     * @param file
     * @param dir
     * @return
     * @throws IOException
     */
    private List<byte[]> readFilesFromDir(File file, String dir) throws IOException {
        List<byte[]> files = new ArrayList<>();
        for (File torrentFile : Objects.requireNonNull(file.listFiles())) {
            Path path = Paths.get(dir + torrentFile.getName());
            byte[] data = Files.readAllBytes(path);
            files.add(data);
        }

        return files;
    }

    /**
     * Read a file from the library
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public byte[] readFile(String filename) throws IOException {
        return read(DIR, filename);
    }

    /**
     * Read a torrent from the torrents folder
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public byte[] readTorrent(String filename) throws IOException {
        return read(TORRENTS_DIR, filename);
    }

    /**
     * Helper method to read a file
     *
     * @param dir
     * @param filename
     * @return
     * @throws IOException
     */
    private byte[] read(String dir, String filename) throws IOException {
        Path path = Paths.get(dir + filename);
        return Files.readAllBytes(path);
    }

    /**
     * Sets the class to testing
     *
     * @return
     */
    public FileIO testing() {
        DIR = LIBRARY_TEST_DIR;
        TORRENTS_DIR = TORRENTS_TEST_DIR;
        File testingDir = new File(LIBRARY_TEST_DIR);
        File torrentsTestingDir = new File(TORRENTS_TEST_DIR);

        if (!testingDir.exists())
            testingDir.mkdir();

        if (!torrentsTestingDir.exists())
            torrentsTestingDir.mkdir();

        folderDir = testingDir;
        torrentsDir = torrentsTestingDir;
        return this;
    }

    /**
     * To ensure thread-safety holder will initialize with a FileIO instance.
     * Everytime File.getInstance() is called will retrieve already existing instance.
     */
    private static class Holder {
        private static final FileIO INSTANCE = new FileIO();
    }
}
