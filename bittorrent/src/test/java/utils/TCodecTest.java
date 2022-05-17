package utils;

import models.MockTorrent;
import models.Torrent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alberto Delgado on 5/9/22
 * @project dsd-final-project-anchitbhatia
 */
class TCodecTest {
    static final Torrent torrent = MockTorrent.get();

    @Test
    void encode() {
        assertDoesNotThrow(() -> TCodec.encode(torrent));
    }

    @Test
    void decode() {
        Torrent copy = TCodec.decode(TCodec.encode(torrent));

        assertEquals(torrent.announce, copy.announce);
        assertEquals(torrent.name, copy.name);
        assertEquals(torrent.pieceLength, copy.pieceLength);
        for (long i = 1; i < 10; i++)
            assertEquals(torrent.pieces.get(i), copy.pieces.get(i));
        assertEquals(torrent.singleFileTorrent, copy.singleFileTorrent);
        assertEquals(torrent.totalSize, copy.totalSize);
        assertEquals(torrent.fileList, copy.fileList);
        assertEquals(torrent.comment, copy.comment);
        assertEquals(torrent.createdBy, copy.createdBy);
        assertEquals(torrent.creationDate, copy.creationDate);
        assertEquals(torrent.announceList, copy.announceList);
        assertEquals(torrent.infoHash, copy.infoHash);
    }
}