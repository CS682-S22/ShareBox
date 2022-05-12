package tracker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import utils.Constants;
import utils.Helper;
import utils.Node;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SwarmDatabaseTest {
    private static SwarmDatabase databaseObj;

    @BeforeAll
    static void setUp() throws IOException {
        databaseObj = new SwarmDatabase();
    }

    @Test
    @Order(1)
    void addPeer1() {
        Node node = new Node("localhost", "1.1.1.1", 1);
        databaseObj.addPeer(node);
        Map<String, Node> peersList = databaseObj.getPeersList();
        Map<String, Constants.status> peerStatus = databaseObj.getPeerStatus();

        assertTrue(peersList.containsKey(node.getIp()));
        assertTrue(peerStatus.containsKey(node.getIp()));

        assertEquals(node, peersList.get(node.getIp()));
        assertEquals(Constants.status.ONLINE, peerStatus.get(node.getIp()));
    }

    @Test
    @Order(2)
    void addPeer2() {
        Node node = new Node("localhost", "1.1.1.2", 2);
        databaseObj.addPeer(node);
        Map<String, Node> peersList = databaseObj.getPeersList();
        Map<String, Constants.status> peerStatus = databaseObj.getPeerStatus();

        assertTrue(peersList.containsKey(node.getIp()));
        assertTrue(peerStatus.containsKey(node.getIp()));

        assertEquals(node, peersList.get(node.getIp()));
        assertEquals(Constants.status.ONLINE, peerStatus.get(node.getIp()));
    }

    @Test
    @Order(3)
    void addPeer3() {
        Node node = new Node("localhost", "1.1.1.3", 3);
        databaseObj.addPeer(node);
        Map<String, Node> peersList = databaseObj.getPeersList();
        Map<String, Constants.status> peerStatus = databaseObj.getPeerStatus();

        assertTrue(peersList.containsKey(node.getIp()));
        assertTrue(peerStatus.containsKey(node.getIp()));

        assertEquals(node, peersList.get(node.getIp()));
        assertEquals(Constants.status.ONLINE, peerStatus.get(node.getIp()));
    }

    @Test
    @Order(4)
    void changePeerStatus1() {
        Node node = new Node("localhost", "1.1.1.4", 4);
        databaseObj.addPeer(node);
        databaseObj.changePeerStatus(node, Constants.status.OFFLINE);
        assertEquals(Constants.status.OFFLINE, databaseObj.getPeerStatus().get(node.getIp()));
    }

    @Test
    @Order(5)
    void changePeerStatus2() {
        Node node = new Node("localhost", "1.1.1.5", 5);
        databaseObj.addPeer(node);
        databaseObj.changePeerStatus(node, Constants.status.ONLINE);
        assertEquals(Constants.status.ONLINE, databaseObj.getPeerStatus().get(node.getIp()));
    }

    @Test
    @Order(6)
    void changePeerStatus3() {
        Node node = new Node("localhost", "1.1.1.6", 6);
        databaseObj.addPeer(node);
        databaseObj.changePeerStatus(node, Constants.status.OFFLINE);
        assertEquals(Constants.status.OFFLINE, databaseObj.getPeerStatus().get(node.getIp()));
        databaseObj.changePeerStatus(node, Constants.status.ONLINE);
        assertEquals(Constants.status.ONLINE, databaseObj.getPeerStatus().get(node.getIp()));
    }

    @Test
    @Order(7)
    void addPieceInfo1() {
        String fileName = "first";
        Long pieceNumber = 1L;
        Node node = new Node("localhost", "1.1.1.4", 4);
        databaseObj.changePeerStatus(node, Constants.status.ONLINE);
        databaseObj.addPieceInfo(fileName, pieceNumber, node);
        Map<Long, List<protos.Node.NodeDetails>> fileInfo = databaseObj.getFileInfo(fileName);
        assertTrue(fileInfo.get(pieceNumber).contains(Helper.getNodeDetailsObject(node)));
    }

    @Test
    @Order(8)
    void addPieceInfo2() {
        String fileName = "first";
        Long pieceNumber = 2L;
        Node node = new Node("localhost", "1.1.1.4", 4);
        databaseObj.changePeerStatus(node, Constants.status.ONLINE);
        databaseObj.addPieceInfo(fileName, pieceNumber, node);
        Map<Long, List<protos.Node.NodeDetails>> fileInfo = databaseObj.getFileInfo(fileName);
        assertTrue(fileInfo.get(pieceNumber).contains(Helper.getNodeDetailsObject(node)));
    }

    @Test
    @Order(9)
    void addPieceInfo3() {
        String fileName = "second";
        Long pieceNumber = 1L;
        Node node = new Node("localhost", "1.1.1.4", 4);
        databaseObj.changePeerStatus(node, Constants.status.ONLINE);
        databaseObj.addPieceInfo(fileName, pieceNumber, node);
        Map<Long, List<protos.Node.NodeDetails>> fileInfo = databaseObj.getFileInfo(fileName);
        assertTrue(fileInfo.get(pieceNumber).contains(Helper.getNodeDetailsObject(node)));
    }

    @Test
    @Order(10)
    void getFileInfo1() {
        String fileName = "third";
        Node node = new Node("localhost", "1.1.1.4", 4);
        databaseObj.changePeerStatus(node, Constants.status.ONLINE);
        databaseObj.addPieceInfo(fileName, 1L, node);
        databaseObj.addPieceInfo(fileName, 2L, node);
        Map<Long, List<protos.Node.NodeDetails>> expectedOutput = new HashMap<>();
        ArrayList<protos.Node.NodeDetails> nodeList = new ArrayList<>();
        nodeList.add(Helper.getNodeDetailsObject(node));
        expectedOutput.put(1L, nodeList);
        expectedOutput.put(2L, nodeList);
        Map<Long, List<protos.Node.NodeDetails>> fileInfo = databaseObj.getFileInfo(fileName);
        assertEquals(expectedOutput, fileInfo);
    }

    @Test
    @Order(11)
    void getFileInfo2() {
        String fileName = "third";
        Node node4 = new Node("localhost", "1.1.1.4", 4);
        Node node5 = new Node("localhost", "1.1.1.5", 5);
        databaseObj.changePeerStatus(node5, Constants.status.ONLINE);
        databaseObj.addPieceInfo(fileName, 1L, node5);
        Map<Long, List<protos.Node.NodeDetails>> expectedOutput = new HashMap<>();
        ArrayList<protos.Node.NodeDetails> nodeList1 = new ArrayList<>();
        nodeList1.add(Helper.getNodeDetailsObject(node4));
        nodeList1.add(Helper.getNodeDetailsObject(node5));
        expectedOutput.put(1L, nodeList1);

        ArrayList<protos.Node.NodeDetails> nodeList2 = new ArrayList<>();
        nodeList2.add(Helper.getNodeDetailsObject(node4));
        expectedOutput.put(2L, nodeList2);
        Map<Long, List<protos.Node.NodeDetails>> fileInfo = databaseObj.getFileInfo(fileName);
        assertEquals(expectedOutput, fileInfo);
    }

    @Test
    @Order(12)
    void getFileInfo3() {
        String fileName = "third";
        Node node4 = new Node("localhost", "1.1.1.4", 4);
        Node node5 = new Node("localhost", "1.1.1.5", 5);
        databaseObj.changePeerStatus(node4, Constants.status.OFFLINE);
        Map<Long, List<protos.Node.NodeDetails>> expectedOutput = new HashMap<>();
        ArrayList<protos.Node.NodeDetails> nodeList1 = new ArrayList<>();
        ArrayList<protos.Node.NodeDetails> nodeList2 = new ArrayList<>();
        nodeList1.add(Helper.getNodeDetailsObject(node5));
        expectedOutput.put(1L, nodeList1);
        expectedOutput.put(2L, nodeList2);
        Map<Long, List<protos.Node.NodeDetails>> fileInfo = databaseObj.getFileInfo(fileName);
        assertEquals(expectedOutput, fileInfo);

        databaseObj.changePeerStatus(node5, Constants.status.OFFLINE);
        nodeList1.clear();
        fileInfo = databaseObj.getFileInfo(fileName);
        assertEquals(expectedOutput, fileInfo);
    }
}