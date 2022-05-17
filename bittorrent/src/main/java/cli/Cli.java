package cli;

import models.Torrent;
import utils.FileIO;
import utils.TCodec;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alberto Delgado on 5/17/22
 * @project bittorrent
 */
public class Cli {
    Prompt prompt;
    List<Torrent> torrents;

    public Cli() {
        prompt = new Prompt();
        try {
            torrents = FileIO.getInstance()
                    .readTorrents()
                    .stream()
                    .map(TCodec::decode)
                    .collect(Collectors.toList());
        } catch (IOException ignored) {
            // no torrents then
        }
    }

    public void start() {
        String name = prompt.readLine("What is your name?");
        System.out.println("hello there " + name);
    }

}
