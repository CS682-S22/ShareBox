package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Alberto Delgado on 5/11/22
 * @author anchit bhatia
 * @project bittorrent
 * <p>
 * Used to encrypt data, more specific, when sharing files, each
 * piece is expected to be SHA-1 encrypted to verify its authenticity
 * and the whole file should be SHA-256
 */
public class Encryption {

    /**
     * SHA-256 encoding
     *
     * @param data
     * @return
     */
    public static byte[] encodeSHA256(byte[] data) {
        return encode(data, "SHA-256");
    }

    /**
     * SHA-1 encoding
     *
     * @param data
     * @return
     */
    public static byte[] encodeSHA1(byte[] data) {
        return encode(data, "SHA-1");
    }

    /**
     * Helper method to encode data
     *
     * @param data
     * @param encoding
     * @return
     */
    private static byte[] encode(byte[] data, String encoding) {
        try {
            MessageDigest d = MessageDigest.getInstance(encoding);
            return d.digest(data);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
