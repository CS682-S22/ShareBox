package tracker;

import utils.Constants;
import utils.Helper;
import utils.Node;
import protos.Node.NodeDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class SwarmDatabase {
    private final ConcurrentHashMap<String, Node> peerList;
    private final ConcurrentHashMap<String, Constants.status> peerStatus;
    private final ConcurrentHashMap<String, ConcurrentHashMap<Long, ConcurrentLinkedDeque<String>>> database;

    public SwarmDatabase() {
        this.peerList = new ConcurrentHashMap<>();
        this.peerStatus = new ConcurrentHashMap<>();
        this.database = new ConcurrentHashMap<>();
    }

    protected void addPeer(Node node) {
        if (!peerList.containsKey(node.getIp())) {
            this.peerList.put(node.getIp(), node);
            this.peerStatus.put(node.getIp(), Constants.status.ONLINE);
        }
    }

    protected void changePeerStatus(Node node, Constants.status newStatus) {
        this.peerStatus.put(node.getIp(), newStatus);
    }

    protected void addPieceInfo(String fileName, Long pieceNumber, Node node) {
        ConcurrentHashMap<Long, ConcurrentLinkedDeque<String>> piecesMap = this.database.getOrDefault(fileName, new ConcurrentHashMap<>());
        ConcurrentLinkedDeque<String> ipList = piecesMap.getOrDefault(pieceNumber, new ConcurrentLinkedDeque<>());
        if (!ipList.contains(node.getIp())) {
            ipList.add(node.getIp());
        }
        piecesMap.put(pieceNumber, ipList);
        this.database.put(fileName, piecesMap);
    }

    protected Map<Long, List<NodeDetails>> getFileInfo(String fileName) {
        if (this.database.containsKey(fileName)) {
            ConcurrentHashMap<Long, ConcurrentLinkedDeque<String>> piecesMap = this.database.get(fileName);
            Map<Long, List<NodeDetails>> fileInfo = new HashMap<>();
            for (Map.Entry<Long, ConcurrentLinkedDeque<String>> item: piecesMap.entrySet()) {
                Long piece = item.getKey();
                ConcurrentLinkedDeque<String> ipList = item.getValue();
                List<NodeDetails> nodesList = new ArrayList<>();
                for (String ip : ipList) {
                    if (peerStatus.getOrDefault(ip, Constants.status.OFFLINE).equals(Constants.status.ONLINE)) {
                        nodesList.add(Helper.getNodeDetailsObject(peerList.get(ip)));
                    }
                }
                fileInfo.put(piece, nodesList);
            }
            return fileInfo;
        }
        return null;
    }

    protected Map<String, Node> getPeersList() {
        return this.peerList;
    }

    protected Map<String, Constants.status> getPeerStatus() {
        return this.peerStatus;
    }
}
