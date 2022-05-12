package client;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static client.TorrentGenerator.getPieceLength;
import static client.TorrentGenerator.getPieces;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
class TorrentGeneratorTest {
    private static byte[] data;

    @BeforeAll
    static void data() {
        data = new byte[101];
        for (int i = 0; i < 101; i++)
            new Random().nextBytes(data);
    }

    @Test
    void fromFile() {
        TorrentGenerator.fromFile("cyberpunk.iso",
                "this is an example torrent",
                "turutupa",
                data
        );
    }

    @Test
    void getPiecesTest() {
        long totalSize = data.length;
        long pieceLength = getPieceLength(data);
        List<String> pieces = getPieces(data, totalSize, pieceLength);
        for (String piece : pieces)
            System.out.println(piece);
    }
}