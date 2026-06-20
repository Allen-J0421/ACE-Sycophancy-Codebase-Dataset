import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class HuffmanCoding {

    private HuffmanCoding() {}

    private static class Node {
        final int frequency;
        final int insertionIndex;
        final Node left, right;

        private Node(int frequency, int insertionIndex, Node left, Node right) {
            this.frequency = frequency;
            this.insertionIndex = insertionIndex;
            this.left = left;
            this.right = right;
        }

        static Node leaf(int frequency, int insertionIndex) {
            return new Node(frequency, insertionIndex, null, null);
        }

        static Node merge(Node left, Node right) {
            return new Node(
                    left.frequency + right.frequency,
                    Math.min(left.insertionIndex, right.insertionIndex),
                    left,
                    right);
        }

        boolean isLeaf() {
            return left == null;
        }
    }

    private static final Comparator<Node> NODE_ORDER = Comparator
            .comparingInt((Node n) -> n.frequency)
            .thenComparingInt(n -> n.insertionIndex);

    public static List<String> huffmanCodes(int[] freq) {
        Node root = buildTree(freq);
        List<String> codes = new ArrayList<>();
        generateCodes(root, codes, new StringBuilder());
        return codes;
    }

    private static Node buildTree(int[] freq) {
        PriorityQueue<Node> pq = new PriorityQueue<>(NODE_ORDER);
        for (int i = 0; i < freq.length; i++) {
            pq.add(Node.leaf(freq[i], i));
        }
        while (pq.size() > 1) {
            pq.add(Node.merge(pq.poll(), pq.poll()));
        }
        return pq.poll();
    }

    private static void generateCodes(Node node, List<String> codes, StringBuilder current) {
        if (node == null) return;

        if (node.isLeaf()) {
            codes.add(current.length() == 0 ? "0" : current.toString());
            return;
        }

        int len = current.length();
        generateCodes(node.left, codes, current.append('0'));
        current.setLength(len);
        generateCodes(node.right, codes, current.append('1'));
        current.setLength(len);
    }

    public static void main(String[] args) {
        int[] freq = {5, 9, 12, 13, 16, 45};
        for (String code : huffmanCodes(freq)) {
            System.out.print(code + " ");
        }
    }
}
