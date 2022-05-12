package utils;

import models.Torrent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.TorrentGenerator.getPieceLength;
import static utils.TorrentGenerator.getPieces;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
class TorrentGeneratorTest {
    private static byte[] data;
    private static final String filename = "cyberpunk.iso";

    @BeforeAll
    static void data() {
        data = new byte[101];
        for (int i = 0; i < 101; i++)
            new Random().nextBytes(data);
    }

    @Test
    void fromFile() throws IOException {
        TorrentGenerator.fromFile(filename,
                "this is an example torrent",
                "turutupa",
                data
        );

        byte[] encoded = FileIO.getInstance().readTorrent("cyberpunk.torrent");
        Torrent t = TCodec.decode(encoded);
        assertEquals(filename, t.name);
    }

    @Test
    void getPiecesTest() {
        long totalSize = data.length;
        long pieceLength = getPieceLength(data);
        List<String> pieces = getPieces(data, totalSize, pieceLength);
        assertEquals((totalSize / pieceLength) + 1, pieces.size());
    }
}