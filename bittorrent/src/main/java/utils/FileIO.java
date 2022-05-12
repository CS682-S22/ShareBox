package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
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
    private static File libraryDir;
    private static File testingDir;
    private static File torrentsDir;
    private static File torrentsTestingDir;

    private FileIO() {
    }

    public static synchronized FileIO getInstance() {
        // Make sure library folder exists.
        libraryDir = new File(LIBRARY_DIR);
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

    public boolean saveFile(String filename, byte[] data) {
        return write(DIR, filename, data);
    }

    public boolean saveTorrent(String filename, byte[] data) {
        return write(TORRENTS_DIR, filename, data);
    }

    private boolean write(String dir, String filename, byte[] data) {
        try (FileOutputStream writer = new FileOutputStream(dir + filename)) {
            writer.write(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<byte[]> readFilesInLibrary() throws IOException {
        return readFilesFromDir(folderDir, DIR);
    }

    public List<byte[]> readTorrents() throws IOException {
        return readFilesFromDir(torrentsDir, TORRENTS_DIR);
    }

    private List<byte[]> readFilesFromDir(File file, String dir) throws IOException {
        List<byte[]> files = new ArrayList<>();
        for (File torrentFile : Objects.requireNonNull(file.listFiles())) {
            Path path = Paths.get(dir + torrentFile.getName());
            byte[] data = Files.readAllBytes(path);
            files.add(data);
        }

        return files;
    }

    public byte[] readFile(String filename) throws IOException {
        return read(DIR, filename);
    }

    public byte[] readTorrent(String filename) throws IOException {
        return read(TORRENTS_DIR, filename);
    }

    private byte[] read(String dir, String filename) throws IOException {
        Path path = Paths.get(dir + filename);
        return Files.readAllBytes(path);
    }

    public FileIO testing() {
        DIR = LIBRARY_TEST_DIR;
        TORRENTS_DIR = TORRENTS_TEST_DIR;
        testingDir = new File(LIBRARY_TEST_DIR);
        torrentsTestingDir = new File(TORRENTS_TEST_DIR);

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
