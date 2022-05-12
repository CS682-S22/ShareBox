package utils;

import models.MockTorrent;
import models.Torrent;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
class FileIOTest {
    private static final Torrent torrent = MockTorrent.get();
    private static final FileIO fileIO = FileIO.getInstance().testing();
    private static final byte[] encoded = Codec.encode(torrent);

    @Test
    void write() throws IOException {
        assertDoesNotThrow(() -> fileIO.saveTorrent(torrent.getName(), encoded));

        for (int i = 0; i < encoded.length; i++)
            assertEquals(encoded[i], fileIO.readTorrent(torrent.getName())[i]);
    }

    @Test
    void readFilesInLibrary() throws IOException {
        fileIO.saveTorrent(torrent.getName(), encoded);
        List<byte[]> files = fileIO.readTorrents();

        assertEquals(1, files.size());

        byte[] mock = files.get(0);
        for (int i = 0; i < encoded.length; i++)
            assertEquals(encoded[i], mock[i]);
    }

    @Test
    void read() throws IOException {
        fileIO.saveTorrent(torrent.getName(), encoded);
        byte[] readFile = fileIO.readTorrent(torrent.getName());

        for (int i = 0; i < encoded.length; i++)
            assertEquals(encoded[i], readFile[i]);
    }

    @Test
    void writeEmpty() throws IOException {
        fileIO.saveFile("EmptyFile.txt", new byte[10]);
        byte[] b = fileIO.readFile("EmptyFile.txt");
        for (int i = 0; i < b.length; i++)
            assertEquals(0, b[i]);
    }
}