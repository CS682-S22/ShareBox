package utils;

import models.Torrent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Alberto Delgado on 5/9/22
 * @project dsd-final-project-anchitbhatia
 */
class CodecTest {
    private static final String announce = "https://test-announce.com";
    private static final String name = "skyrim.iso";
    private static final Long pieceLength = 10000L;
    private static final byte[] piecesBlob = new byte[10];
    private static final List<String> pieces = new ArrayList<>();
    private static final boolean singleFileTorrent = true;
    private static final Long totalSize = 13123123123L;
    private static final List<Torrent.TorrentFile> fileList = null;
    private static final String comment = "this is a comment";
    private static final String createdBy = "turutupa";
    private static final Date creationDate = new Date(System.currentTimeMillis());
    private static final List<String> announceList = null;
    private static final String infoHash = "hash";
    private static final Torrent torrent = new Torrent(
            announce,
            name,
            pieceLength,
            piecesBlob,
            pieces,
            singleFileTorrent,
            totalSize,
            fileList,
            comment,
            createdBy,
            creationDate,
            announceList,
            infoHash
    );

    @BeforeAll
    static void setup() {
        for (int i = 0; i < 10; i++) piecesBlob[i] = (byte) i;
        for (int i = 0; i < 10; i++) pieces.add(String.valueOf(i));
    }

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