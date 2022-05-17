package client;

import models.Torrent;
import org.junit.jupiter.api.Test;
import tracker.Tracker;
import utils.FileIO;
import utils.Globals;
import utils.TCodec;

import java.io.IOException;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
class ClientTest {
    private static final Tracker tracker;
    private static final Client client1;
    private static final String cname1 = "peer1";
    private static final String cip1 = "127.0.0.1";
    private static final int pport1 = 6001;

    private static final Client client2;
    private static final String cname2 = "peer1";
    private static final String cip2 = "127.0.0.1";
    private static final int pport2 = 7001;

    private static final String filename = "jammy-jellyfish-wallpaper.jpg";
<<<<<<< HEAD
    private static final String torrentname = "jammy-jellyfish-wallpaper.torrent";
=======
>>>>>>> 5a1cd77373323da5af9b5af2e85e871aaa9b461f

    static {
        try {
            tracker = new Tracker(
                    Globals.trackerName,
                    Globals.trackerIP,
                    Globals.trackerPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            client1 = new Client(cname1, cip1, pport1).testing();
            client2 = new Client(cname2, cip2, pport2).testing();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void downloadTest() throws InterruptedException, IOException {
        tracker.startServer();
        client1.startServer();
        client2.startServer();

        Thread.sleep(500);

        Torrent torrent = TCodec.decode(
                FileIO
                        .getInstance()
                        .testing()
<<<<<<< HEAD
                        .readTorrent(torrentname));

//        client1.downloadFile(torrent);
=======
                        .readTorrent(filename));

        client1.downloadFile(torrent);
>>>>>>> 5a1cd77373323da5af9b5af2e85e871aaa9b461f

        Thread.sleep(5000);
    }
}