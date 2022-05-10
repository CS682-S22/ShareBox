package utils;

import com.dampcake.bencode.Bencode;
import models.Torrent;

/**
 * @author Alberto Delgado on 5/9/22
 * @project dsd-final-project-anchitbhatia
 */
public class Codec {
    private static Bencode bencode = new Bencode();

    public static byte[] encode(Torrent torrent) {
        return bencode.encode(torrent);
    }

    public static Torrent decode(byte[] encoded) {
        return bencode.decode(encoded, null);
    }
}
