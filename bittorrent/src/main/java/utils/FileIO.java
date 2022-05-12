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
    private static final String LIBRARY_DIR = "./library/";
    private static final String TEST_DIR = "./tests/";
    private static String DIR = LIBRARY_DIR;
    private static File folder;
    private static File library;
    private static File testing;

    private FileIO() {
    }

    public static synchronized FileIO getInstance() {
        // Make sure library folder exists.
        library = new File(LIBRARY_DIR);
        if (!library.exists()) {
            library.mkdir();
        }

        folder = library;
        return Holder.INSTANCE;
    }

    public boolean write(String filename, byte[] data) {
        try (FileOutputStream writer = new FileOutputStream(DIR + filename)) {
            writer.write(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<byte[]> readFilesInLibrary() throws IOException {
        List<byte[]> torrents = new ArrayList<>();
        for (File torrentFile : Objects.requireNonNull(folder.listFiles())) {
            Path path = Paths.get(DIR + torrentFile.getName());
            byte[] data = Files.readAllBytes(path);
            torrents.add(data);
        }

        return torrents;
    }

    public byte[] read(String filename) throws IOException {
        Path path = Paths.get(DIR + filename);
        return Files.readAllBytes(path);
    }

    public FileIO testing() {
        DIR = TEST_DIR;
        testing = new File(TEST_DIR);

        if (!testing.exists())
            testing.mkdir();

        folder = testing;
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
