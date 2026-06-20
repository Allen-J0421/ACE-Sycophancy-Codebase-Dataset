import java.util.ArrayList;
import java.util.Comparator;
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

    static ArrayList<String> huffmanCodes(String s, int[] freq) {
        PriorityQueue<Node> queue = buildLeafQueue(s, freq);
        Node root = buildTree(queue);
        ArrayList<String> codes = new ArrayList<>();
        collectCodes(root, new StringBuilder(), codes);
        return codes;
    }

    private static PriorityQueue<Node> buildLeafQueue(String symbols, int[] frequencies) {
        PriorityQueue<Node> queue = new PriorityQueue<>(NODE_ORDER);
        for (int i = 0; i < symbols.length(); i++) {
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

    public static void main(String[] args) {
        String s = "abcdef";
        int[] freq = {5, 9, 12, 13, 16, 45};
        ArrayList<String> ans = huffmanCodes(s, freq);
        for (String code : ans) {
            System.out.print(code + " ");
        }
    }
}
