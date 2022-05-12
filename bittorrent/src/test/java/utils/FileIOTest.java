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
        assertDoesNotThrow(() -> fileIO.write(torrent.getName(), encoded));

        for (int i = 0; i < encoded.length; i++)
            assertEquals(encoded[i], fileIO.read(torrent.getName())[i]);
    }

    @Test
    void readFilesInLibrary() throws IOException {
        fileIO.write(torrent.getName(), encoded);
        List<byte[]> files = fileIO.readFilesInLibrary();

        assertEquals(1, files.size());

        byte[] mock = files.get(0);
        for (int i = 0; i < encoded.length; i++)
            assertEquals(encoded[i], mock[i]);
    }

    @Test
    void read() throws IOException {
        fileIO.write(torrent.getName(), encoded);
        byte[] readFile = fileIO.read(torrent.getName());

        for (int i = 0; i < encoded.length; i++)
            assertEquals(encoded[i], readFile[i]);
    }
}