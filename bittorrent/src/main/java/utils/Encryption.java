package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
public class Encryption {

    public static byte[] encodeSHA256(byte[] data) {
        return encode(data, "SHA-256");
    }

    public static byte[] encodeSHA1(byte[] data) {
        return encode(data, "SHA-1");
    }

    private static byte[] encode(byte[] data, String encoding) {
        try {
            MessageDigest d = MessageDigest.getInstance(encoding);
            return d.digest(data);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
