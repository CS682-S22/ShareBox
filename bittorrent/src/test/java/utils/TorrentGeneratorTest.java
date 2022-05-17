package utils;

import models.Torrent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.TorrentGenerator.getPieceLength;
import static utils.TorrentGenerator.getPieces;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
class TorrentGeneratorTest {
    private static byte[] data;
    private static final String filename = "jammy-jellyfish-wallpaper.jpg";
    private static final String comment = "This is an amazing wallpaper! But low quality";
    private static final String createdBy = "turutupa";

    @BeforeAll
    static void data() throws IOException {
        FileIO fileIO = FileIO.getInstance().testing();
        data = fileIO.readFile(filename);
    }

    @Test
    void fromFile() throws IOException {
        TorrentGenerator.fromFile(filename,
                "this is an example torrent",
                "turutupa",
                data
        );

        byte[] encoded = FileIO
                .getInstance()
                .testing()
                .readTorrent(Helper.getTorrentName(filename));
        Torrent t = TCodec.decode(encoded);
        assertEquals(filename, t.name);
    }

    @Test
    void getPiecesTest() {
        long totalSize = data.length;
        long pieceLength = getPieceLength(data);
        Map<Long, String> pieces = getPieces(data, totalSize, pieceLength);
        assertEquals((totalSize / pieceLength) + 1, pieces.size());
    }

    @Test
    void generateTorrent() throws IOException {
        Torrent torrent = TorrentGenerator.createTorrent(filename, comment, createdBy, data);
        String torrentName = Helper.getTorrentName(filename);
        byte[] data = TCodec.encode(torrent);
        FileIO.getInstance()
                .testing()
                .saveTorrent(torrentName, data);

        byte[] d = FileIO.getInstance()
                .testing()
                .readTorrent(torrentName);
        for (int i = 0; i < d.length; i++)
            assertEquals(d[i], data[i]);

        for (int i = 0; i < data.length; i++)
            assertEquals(d[i], data[i]);
    }
}