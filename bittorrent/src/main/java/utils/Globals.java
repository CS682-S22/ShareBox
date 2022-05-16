package utils;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
public class Globals {
    public static String trackerName = "Tracker";
    public static String trackerIP = "127.0.0.1"; // default ip
    public static int trackerPort = 5000; // default port
    public static Long PIECE_LENGTH_MAX = (long) Math.pow(2, 10);
}
