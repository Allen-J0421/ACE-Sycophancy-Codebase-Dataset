import java.util.*;

public class HuffmanCoding {

    private static class Node {
        int frequency;
        int insertionIndex;
        Node left, right;

        Node(int frequency, int insertionIndex) {
            this.frequency = frequency;
            this.insertionIndex = insertionIndex;
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

    static ArrayList<String> huffmanCodes(String s, int[] freq) {
        int n = s.length();

        PriorityQueue<Node> pq = new PriorityQueue<>(NODE_ORDER);
        for (int i = 0; i < n; i++) {
            pq.add(new Node(freq[i], i));
        }

        if (n == 1) {
            return new ArrayList<>(Collections.singletonList("0"));
        }

        while (pq.size() >= 2) {
            Node left = pq.poll();
            Node right = pq.poll();
            pq.add(new Node(left, right));
        }

        ArrayList<String> codes = new ArrayList<>();
        generateCodes(pq.poll(), codes, "");
        return codes;
    }

    private static void generateCodes(Node node, ArrayList<String> codes, String current) {
        if (node == null) return;

        if (node.isLeaf()) {
            codes.add(current.isEmpty() ? "0" : current);
            return;
        }

        generateCodes(node.left, codes, current + '0');
        generateCodes(node.right, codes, current + '1');
    }

    public static void main(String[] args) {
        String s = "abcdef";
        int[] freq = {5, 9, 12, 13, 16, 45};
        ArrayList<String> codes = huffmanCodes(s, freq);
        for (String code : codes) {
            System.out.print(code + " ");
        }
    }
}
