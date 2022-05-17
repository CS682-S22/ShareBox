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
    private static final byte[] encoded = TCodec.encode(torrent);

    @Test
    void write() throws IOException {
        assertDoesNotThrow(() -> fileIO.saveTorrent(torrent.getTorrentName(), encoded));

        for (int i = 0; i < encoded.length; i++)
            assertEquals(encoded[i], fileIO.readTorrent(torrent.getTorrentName())[i]);
    }

    @Test
    void readFilesInLibrary() throws IOException {
        fileIO.saveTorrent(torrent.getTorrentName(), encoded);
        List<byte[]> files = fileIO.readTorrents();

        assertEquals(2, files.size());

        byte[] mock = files.get(0);
        for (int i = 0; i < encoded.length; i++)
            assertEquals(encoded[i], mock[i]);
    }

    @Test
    void read() throws IOException {
        fileIO.saveTorrent(torrent.getTorrentName(), encoded);
        byte[] readFile = fileIO.readTorrent(torrent.getTorrentName());

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

    @Test
    void readPiece() throws IOException {
        String filename = "jammy-jellyfish-wallpaper.jpg";
        String torrentname = "jammy-jellyfish-wallpaper.torrent";
        byte[] data = fileIO.readFile(filename);
        Torrent torrent = TCodec.decode(fileIO.readTorrent(torrentname));
        long pieceLength = torrent.pieceLength;
        System.out.println("length: " + data.length);

        for (int i = 0; i < data.length; i += pieceLength) {
            byte[] piece = fileIO.readPiece(torrent, i);
            int z = 0;
            for (int j = i; j < pieceLength; j++) {
                if (j == data.length) return;
                assertEquals(data[j], piece[z++]);
            }
        }
    }
}