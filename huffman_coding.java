import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;

class HuffmanCoding {

    private static final String SINGLE_SYMBOL_CODE = "0";

    private static final Comparator<Node> NODE_ORDER = Comparator
            .comparingInt((Node node) -> node.frequency)
            .thenComparingInt(node -> node.firstSymbolIndex);

    private static final class Node {
        private final int frequency;
        private final int firstSymbolIndex;
        private final Node left;
        private final Node right;

        private Node(int frequency, int symbolIndex) {
            this.frequency = frequency;
            this.firstSymbolIndex = symbolIndex;
            this.left = null;
            this.right = null;
        }

        private Node(Node left, Node right) {
            this.frequency = left.frequency + right.frequency;
            this.firstSymbolIndex = Math.min(left.firstSymbolIndex, right.firstSymbolIndex);
            this.left = left;
            this.right = right;
        }

        private boolean isLeaf() {
            return left == null && right == null;
        }
    }

    static ArrayList<String> huffmanCodes(String s, int[] freq) {
        validateInput(s, freq);

        if (s.isEmpty()) {
            return new ArrayList<>();
        }

        Node root = buildTree(freq);
        ArrayList<String> codes = new ArrayList<>();
        collectCodes(root, "", codes);
        return codes;
    }

    private static void validateInput(String s, int[] freq) {
        Objects.requireNonNull(s, "Input string must not be null");
        Objects.requireNonNull(freq, "Frequency array must not be null");

        if (s.length() != freq.length) {
            throw new IllegalArgumentException("Input string and frequency array must have the same length");
        }
    }

    private static Node buildTree(int[] frequencies) {
        PriorityQueue<Node> nodes = new PriorityQueue<>(NODE_ORDER);

        for (int i = 0; i < frequencies.length; i++) {
            nodes.add(new Node(frequencies[i], i));
        }

        while (nodes.size() > 1) {
            Node left = nodes.poll();
            Node right = nodes.poll();
            nodes.add(new Node(left, right));
        }

        return nodes.poll();
    }

    private static void collectCodes(Node root, String prefix, ArrayList<String> codes) {
        if (root == null) {
            return;
        }

        if (root.isLeaf()) {
            codes.add(prefix.isEmpty() ? SINGLE_SYMBOL_CODE : prefix);
            return;
        }

        collectCodes(root.left, prefix + '0', codes);
        collectCodes(root.right, prefix + '1', codes);
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
