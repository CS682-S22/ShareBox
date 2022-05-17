package utils;

public class ApplicationConfig {
    NodeConfig config;
    String type;
    TorrentMetadata torrentMetadata;

    public void setNodeConfig(NodeConfig config) {
        this.config = config;
    }

    public NodeConfig getNodeConfig() {
        return config;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getType() {
        return type;
    }

    public TorrentMetadata getTorrentMetadata() {
        return torrentMetadata;
    }

    public void setTorrentMetadata(String fileName, String comment, String createdBy) {
        torrentMetadata = new TorrentMetadata(fileName, comment, createdBy);
    }

    public String getFileName() {
        return torrentMetadata.fileName;
    }

    public String getComment() {
        return torrentMetadata.comment;
    }

    public String getCreatedBy() {
        return torrentMetadata.createdBy;
    }

    private static class TorrentMetadata {
        String fileName;
        String comment;
        String createdBy;

        public TorrentMetadata(String fileName, String comment, String createdBy) {
            this.fileName = fileName;
            this.comment = comment;
            this.createdBy = createdBy;
        }
    }
}
