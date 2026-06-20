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

        Node(int frequency, int insertionIndex) {
            this.frequency = frequency;
            this.insertionIndex = insertionIndex;
            this.left = null;
            this.right = null;
        }

        Node(Node left, Node right) {
            this.frequency = left.frequency + right.frequency;
            this.insertionIndex = Math.min(left.insertionIndex, right.insertionIndex);
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    private static final Comparator<Node> NODE_ORDER = Comparator
            .comparingInt((Node n) -> n.frequency)
            .thenComparingInt(n -> n.insertionIndex);

    static List<String> huffmanCodes(String s, int[] freq) {
        int n = freq.length;

        PriorityQueue<Node> pq = new PriorityQueue<>(NODE_ORDER);
        for (int i = 0; i < n; i++) {
            pq.add(new Node(freq[i], i));
        }

        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            pq.add(new Node(left, right));
        }

        List<String> codes = new ArrayList<>();
        generateCodes(pq.poll(), codes, new StringBuilder());
        return codes;
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
        String s = "abcdef";
        int[] freq = {5, 9, 12, 13, 16, 45};
        List<String> codes = huffmanCodes(s, freq);
        for (String code : codes) {
            System.out.print(code + " ");
        }
    }
}
