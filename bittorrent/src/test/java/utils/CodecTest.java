package utils;

import models.MockTorrent;
import models.Torrent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alberto Delgado on 5/9/22
 * @project dsd-final-project-anchitbhatia
 */
class CodecTest {
    static final Torrent torrent = MockTorrent.get();

    @Test
    void encode() {
        Assertions.assertDoesNotThrow(() -> Codec.encode(torrent));
    }

    @Test
    void decode() {
        Torrent copy = Codec.decode(Codec.encode(torrent));

        assertEquals(torrent.announce, copy.announce);
        assertEquals(torrent.name, copy.name);
        assertEquals(torrent.pieceLength, copy.pieceLength);
        for (int i = 0; i < 10; i++)
            assertEquals(torrent.piecesBlob[i], copy.piecesBlob[i]);
        for (int i = 0; i < 10; i++)
            assertEquals(torrent.pieces, copy.pieces);
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