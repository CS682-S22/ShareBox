package tracker;

import protos.Node.NodeDetails;
import utils.Constants;
import utils.Helper;
import utils.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author alberto delgado
 * @author anchit bhatia
 * <p>
 * Swarm Database!
 * <p>
 * Stores information of online peers as well as information on the currently shared torrents
 * including what node has what piece
 */
public class SwarmDatabase {
    final Map<String, Node> peerList;
    final Map<String, Constants.Status> peerStatus;
    final Map<String, ConcurrentHashMap<Long, ConcurrentLinkedDeque<String>>> database;

    public SwarmDatabase() {
        this.peerList = new ConcurrentHashMap<>();
        this.peerStatus = new ConcurrentHashMap<>();
        this.database = new ConcurrentHashMap<>();
    }

    /**
     * Adds peer
     *
     * @param node
     */
    protected void addPeer(Node node) {
        String peerId = Helper.getPeerId(node);
        if (!peerList.containsKey(peerId)) {
            this.peerList.put(peerId, node);
            this.peerStatus.put(peerId, Constants.Status.ONLINE);
        }
    }

    /**
     * Removes peer
     *
     * @param id
     */
    protected void removePeer(String id) {
        this.peerList.remove(id);
        this.peerStatus.put(id, Constants.Status.OFFLINE);
    }

    /**
     * Updates peer status ONLINE/OFFLINE
     *
     * @param node
     * @param newStatus
     */
    protected void changePeerStatus(Node node, Constants.Status newStatus) {
        this.peerStatus.put(Helper.getPeerId(node), newStatus);
    }

    /**
     * Adds information about a file piece
     *
     * @param fileName
     * @param pieceNumber
     * @param node
     */
    protected void addPieceInfo(String fileName, Long pieceNumber, Node node) {
        ConcurrentHashMap<Long, ConcurrentLinkedDeque<String>> piecesMap = this.database.getOrDefault(fileName, new ConcurrentHashMap<>());
        ConcurrentLinkedDeque<String> ipList = piecesMap.getOrDefault(pieceNumber, new ConcurrentLinkedDeque<>());
        String peerId = Helper.getPeerId(node);
        if (!ipList.contains(peerId)) {
            ipList.add(peerId);
        }
        piecesMap.put(pieceNumber, ipList);
        this.database.put(fileName, piecesMap);
    }

    /**
     * Gets all file information
     *
     * @param fileName
     * @return
     */
    protected Map<Long, List<NodeDetails>> getFileInfo(String fileName) {
        if (this.database.containsKey(fileName)) {
            ConcurrentHashMap<Long, ConcurrentLinkedDeque<String>> piecesMap = this.database.get(fileName);
            Map<Long, List<NodeDetails>> fileInfo = new HashMap<>();
            for (Map.Entry<Long, ConcurrentLinkedDeque<String>> item : piecesMap.entrySet()) {
                Long piece = item.getKey();
                ConcurrentLinkedDeque<String> ipList = item.getValue();
                List<NodeDetails> nodesList = new ArrayList<>();
                for (String ip : ipList) {
                    if (peerStatus.getOrDefault(ip, Constants.Status.OFFLINE).equals(Constants.Status.ONLINE)) {
                        nodesList.add(Helper.getNodeDetailsObject(peerList.get(ip)));
                    }
                }
                fileInfo.put(piece, nodesList);
            }
            return fileInfo;
        }
        return null;
    }

    /**
     * Peer list getter
     *
     * @return
     */
    protected Map<String, Node> getPeersList() {
        return this.peerList;
    }

    /**
     * Peer status getter
     *
     * @return
     */
    protected Map<String, Constants.Status> getPeerStatus() {
        return this.peerStatus;
    }
}
