package utils;

import models.MockTorrent;
import models.Torrent;
import org.junit.jupiter.api.Test;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
class FileIOTest {
    private static final Torrent torrent = MockTorrent.get();
    private static final FileIO fileIO = FileIO.getInstance();

    @Test
    void write() {
        fileIO.write();
    }

    @Test
    void readFilesInLibrary() {
    }

    @Test
    void read() {
    }
}