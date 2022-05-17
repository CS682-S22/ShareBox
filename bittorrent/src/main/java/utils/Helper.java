package utils;

import com.google.gson.Gson;
import protos.Node.NodeDetails;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;

public class Helper {

    public static void exit(String msg){
        System.out.println("\nError: " + msg);
        System.out.println("What do you want to run?");
        System.out.println("Use --client to run a client");
        System.out.println("Use --tracker to run a tracker server");
        System.out.println("Use --new-torrent <filename> <comment> <createdBy> to generate a new torrent file");
        System.exit(1);
    }

    public static ApplicationConfig parseArgs(String[] args) {
        if (args.length == 0 || args.length > 4) {
            exit("Invalid args");
        }
        String type = args[0];
        ApplicationConfig appConfig = new ApplicationConfig();
        if (Objects.equals(args[0], Constants.TYPE_NEW_TORRENT)) {
            appConfig.setTorrentMetadata(args[1], args[2], args[3]);
        }
        else {
            String file = args[1];
            Gson gson = new Gson();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                NodeConfig config = gson.fromJson(reader, NodeConfig.class);
                appConfig.setNodeConfig(config);

                if (Objects.equals(type, Constants.TYPE_CLIENT)) {
                    appConfig.setType(Constants.TYPE_CLIENT);
                } else if (Objects.equals(args[0], Constants.TYPE_TRACKER)) {
                    appConfig.setType(Constants.TYPE_TRACKER);
                }
            } catch (FileNotFoundException e) {
                exit("File not found");
            }
        }

//        if (Objects.equals(type, "--client")) {
//            clientNode();
//        } else if (Objects.equals(args[0], "--client2")) {
//            clientNode(args[1]);
//        } else if (Objects.equals(args[0], "--client3")) {
//            clientNode3(args[1]);
//        } else if (Objects.equals(args[0], "--new-torrent")) {
//            generateTorrent(args[1], args[2], args[3]);
//        } else if (Objects.equals(args[0], "--tracker")) {
//            trackerNode();
//        }
//
//        BufferedReader reader = new BufferedReader(new FileReader(file));
//        Object config = gson.fromJson(reader, classType);
        return appConfig;
    }

    public static NodeDetails getNodeDetailsObject(Node node) {
        return NodeDetails.newBuilder().
                setHostname(node.getHostname()).
                setPort(node.getPort()).
                setIp(node.getIp()).
                build();
    }

    public static Node getNodeObject(NodeDetails nodeDetails) {
        return new Node(nodeDetails.getHostname(), nodeDetails.getIp(), nodeDetails.getPort());
    }

    public static String getIpFromSocket(Socket socket) {
        return (((InetSocketAddress) socket.getRemoteSocketAddress())
                .getAddress())
                .toString()
                .replace("/", "");
    }

    public static String getTorrentName(String filename) {
        int i = filename.indexOf('.');
        if (i > 0)
            filename = filename.substring(0, i);

        return filename + Constants.TORRENT_EXT;
    }

    public static String getPeerId(Node node) {
        return node.getIp() + ":" + node.getPort();
    }

}
