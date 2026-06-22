import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public final class HuffmanCoding {
    private HuffmanCoding() {
        // Utility class.
    }

    private static final class TreeNode {
        final int frequency;
        final int symbolIndex;
        final TreeNode left;
        final TreeNode right;

        TreeNode(int frequency, int symbolIndex) {
            this.frequency = frequency;
            this.symbolIndex = symbolIndex;
            this.left = null;
            this.right = null;
        }

        TreeNode(TreeNode left, TreeNode right) {
            this.frequency = left.frequency + right.frequency;
            this.symbolIndex = Math.min(left.symbolIndex, right.symbolIndex);
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    private static final Comparator<TreeNode> NODE_ORDER =
            Comparator.comparingInt((TreeNode node) -> node.frequency)
                    .thenComparingInt(node -> node.symbolIndex);

    public static ArrayList<String> huffmanCodes(String s, int[] freq) {
        validateInput(s, freq);

        ArrayList<String> codes = createEmptyCodeList(freq.length);
        if (freq.length <= 1) {
            if (freq.length == 1) {
                codes.set(0, "0");
            }
            return codes;
        }

        collectCodes(buildTree(freq), new StringBuilder(), codes);
        return codes;
    }

    private static void validateInput(String s, int[] freq) {
        if (s == null || freq == null) {
            throw new IllegalArgumentException("Input string and frequency array must be non-null.");
        }
        if (s.length() != freq.length) {
            throw new IllegalArgumentException("String length and frequency array length must match.");
        }
    }

    private static ArrayList<String> createEmptyCodeList(int size) {
        return new ArrayList<>(Collections.nCopies(size, ""));
    }

    private static TreeNode buildTree(int[] freq) {
        PriorityQueue<TreeNode> queue = new PriorityQueue<>(NODE_ORDER);
        for (int i = 0; i < freq.length; i++) {
            queue.add(new TreeNode(freq[i], i));
        }

        while (queue.size() > 1) {
            TreeNode left = queue.poll();
            TreeNode right = queue.poll();
            queue.add(new TreeNode(left, right));
        }

        return queue.peek();
    }

    private static void collectCodes(TreeNode node, StringBuilder path, ArrayList<String> codes) {
        if (node == null) {
            return;
        }

        if (node.isLeaf()) {
            codes.set(node.symbolIndex, path.length() == 0 ? "0" : path.toString());
            return;
        }

        path.append('0');
        collectCodes(node.left, path, codes);
        path.setLength(path.length() - 1);

        path.append('1');
        collectCodes(node.right, path, codes);
        path.setLength(path.length() - 1);
    }

    public static void main(String[] args) {
        HuffmanCodingDemo.main(args);
    }
}
