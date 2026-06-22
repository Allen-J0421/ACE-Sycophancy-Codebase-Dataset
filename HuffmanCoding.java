import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public final class HuffmanCoding {
    private HuffmanCoding() {
        // Utility class.
    }

    private static final class Node {
        final int frequency;
        final int order;
        final Node left;
        final Node right;

        Node(int frequency, int order) {
            this.frequency = frequency;
            this.order = order;
            this.left = null;
            this.right = null;
        }

        Node(Node left, Node right) {
            this.frequency = left.frequency + right.frequency;
            this.order = Math.min(left.order, right.order);
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    private static final Comparator<Node> NODE_ORDER =
            Comparator.comparingInt((Node node) -> node.frequency)
                    .thenComparingInt(node -> node.order);

    public static ArrayList<String> huffmanCodes(String s, int[] freq) {
        if (s == null || freq == null) {
            throw new IllegalArgumentException("Input string and frequency array must be non-null.");
        }
        if (s.length() != freq.length) {
            throw new IllegalArgumentException("String length and frequency array length must match.");
        }

        int n = freq.length;
        ArrayList<String> codes = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            codes.add("");
        }
        if (n == 0) {
            return codes;
        }
        if (n == 1) {
            codes.set(0, "0");
            return codes;
        }

        PriorityQueue<Node> queue = new PriorityQueue<>(NODE_ORDER);
        for (int i = 0; i < n; i++) {
            queue.add(new Node(freq[i], i));
        }

        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            queue.add(new Node(left, right));
        }

        assignCodes(queue.peek(), new StringBuilder(), codes);
        return codes;
    }

    private static void assignCodes(Node node, StringBuilder path, ArrayList<String> codes) {
        if (node == null) {
            return;
        }

        if (node.isLeaf()) {
            codes.set(node.order, path.length() == 0 ? "0" : path.toString());
            return;
        }

        path.append('0');
        assignCodes(node.left, path, codes);
        path.setLength(path.length() - 1);

        path.append('1');
        assignCodes(node.right, path, codes);
        path.setLength(path.length() - 1);
    }

    public static void main(String[] args) {
        String symbols = "abcdef";
        int[] frequencies = {5, 9, 12, 13, 16, 45};
        ArrayList<String> codes = huffmanCodes(symbols, frequencies);
        for (String code : codes) {
            System.out.print(code + " ");
        }
    }
}
