package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
public class MockTorrent {
    private static final String announce = "https://test-announce.com";
    private static final String name = "skyrim.iso";
    private static final Long pieceLength = 10000L;
    private static final byte[] piecesBlob = mockArray();
    private static final List<String> pieces = mockList();
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

    private static List<String> mockList() {
        List<String> l = new ArrayList<>();
        for (int i = 0; i < 10; i++) l.add(i, String.valueOf(i));
        return l;
    }

    private static byte[] mockArray() {
        byte[] a = new byte[10];
        for (int i = 0; i < 10; i++) a[i] = (byte) i;
        return a;
    }

    public static Torrent get() {
        return torrent;
    }
}