import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class HuffmanCoding {
    private static final String SINGLE_SYMBOL_CODE = "0";
    private static final Comparator<Node> NODE_ORDER =
            Comparator.comparingInt((Node node) -> node.frequency)
                    .thenComparingInt(node -> node.order);

    private static final class Node {
        private final int frequency;
        private final int order;
        private final Node left;
        private final Node right;

        private Node(int frequency, int order) {
            this(frequency, order, null, null);
        }

        private Node(Node left, Node right) {
            this(left.frequency + right.frequency, Math.min(left.order, right.order), left, right);
        }

        private Node(int frequency, int order, Node left, Node right) {
            this.frequency = frequency;
            this.order = order;
            this.left = left;
            this.right = right;
        }

        private boolean isLeaf() {
            return left == null && right == null;
        }
    }

    static ArrayList<String> huffmanCodes(String symbols, int[] frequencies) {
        validateInputs(symbols, frequencies);
        if (symbols.isEmpty()) {
            return new ArrayList<>();
        }

        PriorityQueue<Node> queue = buildLeafQueue(frequencies);
        Node root = buildTree(queue);
        return buildCodes(root);
    }

    private static void validateInputs(String symbols, int[] frequencies) {
        if (symbols == null) {
            throw new IllegalArgumentException("symbols must not be null");
        }
        if (frequencies == null) {
            throw new IllegalArgumentException("frequencies must not be null");
        }
        if (symbols.length() != frequencies.length) {
            throw new IllegalArgumentException("symbols and frequencies must be the same length");
        }
    }

    private static PriorityQueue<Node> buildLeafQueue(int[] frequencies) {
        PriorityQueue<Node> queue = new PriorityQueue<>(NODE_ORDER);
        for (int i = 0; i < frequencies.length; i++) {
            queue.add(new Node(frequencies[i], i));
        }
        return queue;
    }

    private static Node buildTree(PriorityQueue<Node> queue) {
        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            queue.add(new Node(left, right));
        }
        return queue.peek();
    }

    private static ArrayList<String> buildCodes(Node root) {
        ArrayList<String> codes = new ArrayList<>();
        collectCodes(root, new StringBuilder(), codes);
        return codes;
    }

    private static void collectCodes(Node node, StringBuilder path, ArrayList<String> codes) {
        if (node == null) {
            return;
        }

        if (node.isLeaf()) {
            codes.add(path.length() == 0 ? SINGLE_SYMBOL_CODE : path.toString());
            return;
        }

        path.append('0');
        collectCodes(node.left, path, codes);
        path.deleteCharAt(path.length() - 1);

        path.append('1');
        collectCodes(node.right, path, codes);
        path.deleteCharAt(path.length() - 1);
    }

    private static void printCodes(List<String> codes) {
        for (String code : codes) {
            System.out.print(code + " ");
        }
    }

    public static void main(String[] args) {
        String symbols = "abcdef";
        int[] frequencies = {5, 9, 12, 13, 16, 45};
        ArrayList<String> codes = huffmanCodes(symbols, frequencies);
        printCodes(codes);
    }
}
