package models;

import java.util.*;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
public class MockTorrent {
    private static final String announce = "https://test-announce.com";
    private static final String name = "skyrim.iso";
    private static final Long pieceLength = 10000L;
    private static final Map<Long, String> pieces = mockMap();
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

    private static Map<Long, String> mockMap() {
        Map<Long, String> l = new HashMap<>();
        for (int i = 1; i < 10; i++) l.put((long) i, String.valueOf(i));
        return l;
    }

    public static Torrent get() {
        return torrent;
    }
}