package models;

import java.util.Date;
import java.util.List;

/**
 * @author Alberto Delgado on 5/9/22
 * @project dsd-final-project-anchitbhatia
 * <p>
 * Basic torrent data structure.
 * <p>
 * Used this as a template: https://github.com/m1dnight/torrent-parser/blob/master/src/main/java/be/christophedetroyer/torrent/Torrent.java
 */
public class Torrent {
    public final String announce;
    public final String name;
    public final Long pieceLength;
    public final byte[] piecesBlob;
    public final List<String> pieces;
    public final boolean singleFileTorrent;
    public final Long totalSize;
    public final List<TorrentFile> fileList;
    public final String comment;
    public final String createdBy;
    public final Date creationDate;
    public final List<String> announceList;
    public final String infoHash;

    public Torrent(
            String announce,
            String name,
            Long pieceLength,
            byte[] piecesBlob,
            List<String> pieces,
            boolean singleFileTorrent,
            Long totalSize,
            List<TorrentFile> fileList,
            String comment,
            String createdBy,
            Date creationDate,
            List<String> announceList,
            String infoHash
    ) {
        this.announce = announce;
        this.name = name;
        this.pieceLength = pieceLength;
        this.piecesBlob = piecesBlob;
        this.pieces = pieces;
        this.singleFileTorrent = singleFileTorrent;
        this.totalSize = totalSize;
        this.fileList = fileList;
        this.comment = comment;
        this.createdBy = createdBy;
        this.creationDate = creationDate;
        this.announceList = announceList;
        this.infoHash = infoHash;
    }

    public class TorrentFile {
        public final Long fileLength;
        public final List<String> fileDirs;

        public TorrentFile(Long fileLength, List<String> fileDirs) {
            this.fileLength = fileLength;
            this.fileDirs = fileDirs;
        }
    }
}
