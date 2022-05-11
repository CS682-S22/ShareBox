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
    private static File library;

    private FileIO() {
    }

    public static synchronized FileIO getInstance() {
        // Make sure library folder exists.
        library = new File(LIBRARY_DIR);
        if (!library.exists())
            library.mkdir();

        return Holder.INSTANCE;
    }

    public boolean write(String filename, byte[] data) {
        try (FileOutputStream writer = new FileOutputStream(LIBRARY_DIR + filename)) {
            writer.write(data);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<byte[]> readFilesInLibrary() throws IOException {
        List<byte[]> torrents = new ArrayList<>();
        for (File torrentFile : Objects.requireNonNull(library.listFiles())) {
            Path path = Paths.get(LIBRARY_DIR + torrentFile.getName());
            byte[] data = Files.readAllBytes(path);
            torrents.add(data);
        }

        return torrents;
    }

    public byte[] read(String filename) throws IOException {
        Path path = Paths.get(LIBRARY_DIR + filename);
        return Files.readAllBytes(path);
    }

    /**
     * To ensure thread-safety holder will initialize with a FileIO instance.
     * Everytime File.getInstance() is called will retrieve already existing instance.
     */
    private static class Holder {
        private static final FileIO INSTANCE = new FileIO();
    }
}
