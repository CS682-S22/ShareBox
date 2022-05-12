package client;

import org.junit.jupiter.api.Test;
import utils.ConnectionException;

import java.io.IOException;

/**
 * @author Alberto Delgado on 5/11/22
 * @project bittorrent
 */
class ClientTest {

    @Test
    void start() {
        Client peer;
        try {
            peer = new Client("Peer1", "127.0.0.1", 5000);
        } catch (IOException | ConnectionException e) {
            peer = null;
        }

        if (peer == null) return;
    }
}