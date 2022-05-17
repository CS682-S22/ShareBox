package utils;

/**
 * @author Alberto Delgado on 5/11/22
 * @author anchit bhatia
 * @project bittorrent
 * <p>
 * Global modifiable variables
 */
public class Globals {
    public static String trackerName = "hostname";
    public static String trackerIP = "127.0.0.1"; // default ip
    public static int trackerPort = 5000; // default port
    public static Long PIECE_LENGTH_MAX = (long) Math.pow(2, 10);
}
