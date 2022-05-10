package models;

/**
 * @author Alberto Delgado on 5/9/22
 * @project dsd-final-project-anchitbhatia
 */
public class Torrent {
    public final String announce;
    public final Info info;

    public Torrent(
            String announce, // the URl of the tracker
            Info info        // file info -> varies if multiple files
    ) {
        this.announce = announce;
        this.info = info;
    }

    public class Info {
        public final int length;
        public final String name;
        public final int pieceLength;
        public final int[] pieces;

        public Info(
                int length,      // size of file in bytes
                String name,     // filename where file is to be saved
                int pieceLength, // number of bytes per piece
                int[] pieces     // concatenation of each piece's SHA-1
        ) {
            this.length = length;
            this.name = name;
            this.pieceLength = pieceLength;
            this.pieces = pieces;
        }
    }
}
