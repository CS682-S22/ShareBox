package utils;

import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;
import models.Torrent;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alberto Delgado on 5/9/22
 * @project dsd-final-project-anchitbhatia
 */
public class Codec {
    private static Bencode bencode = new Bencode();

    public static byte[] encode(Torrent torrent) {
        HashMap<String, Object> data = new HashMap<>() {{
            put("announce", torrent.announce);
            put("name", torrent.name);
            put("pieceLength", torrent.pieceLength);
            put("piecesBlob", new String(torrent.piecesBlob, StandardCharsets.UTF_8));
            put("pieces", torrent.pieces);
            put("singleFileTorrent", torrent.singleFileTorrent);
            put("totalSize", torrent.totalSize);
            put("comment", torrent.comment);
            put("createdBy", torrent.createdBy);
            put("creationDate", torrent.creationDate.getTime());
            put("infoHash", torrent.infoHash);
        }};

        if (!torrent.singleFileTorrent)
            data.put("fileList", torrent.fileList);

        if (torrent.announceList != null)
            data.put("announceList", torrent.announceList);

        return bencode.encode(data);
    }

    public static Torrent decode(byte[] encoded) {
        Map<String, Object> decoded = bencode.decode(encoded, Type.DICTIONARY);

        String announce = (String) decoded.get("announce");
        String name = (String) decoded.get("name");
        long pieceLength = (Long) decoded.get("pieceLength");
        byte[] piecesBlob = ((String) decoded.get("piecesBlob")).getBytes();
        List<String> pieces = (List<String>) decoded.get("pieces");
        boolean singleFileTorrent = Boolean.valueOf((String) decoded.get("singleFileTorrent"));
        long totalSize = (Long) decoded.get("totalSize");
        List<Torrent.TorrentFile> fileList = (List<Torrent.TorrentFile>) decoded.get("fileList");
        String comment = (String) decoded.get("comment");
        String createdBy = (String) decoded.get("createdBy");
        Date creationDate = new Date((Long) decoded.get("creationDate"));
        List<String> announceList = (List<String>) decoded.get("announceList");
        String infoHash = (String) decoded.get("infoHash");

        return new Torrent(
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
    }
}
