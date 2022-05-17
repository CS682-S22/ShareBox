package utils;

public class Constants {
    public static enum Status {
        ONLINE,
        OFFLINE
    }

    public final static String KEY_NODE = "key_node";
    public final static String KEY_STATUS = "key_status";
    public final static String TORRENT_EXT = ".torrent";
    public final static long HEARTBEAT_INTERVAL_MS = 5000L;
    public final static int MAX_CONCURRENT_DOWNLOADS = 10;
}
