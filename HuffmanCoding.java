import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public final class HuffmanCoding {
    private static final char LEFT_BIT = '0';
    private static final char RIGHT_BIT = '1';

    private HuffmanCoding() {
        // Utility class.
    }

    private static final class HuffmanNode {
        final int frequency;
        final int symbolIndex;
        final HuffmanNode left;
        final HuffmanNode right;

        HuffmanNode(int frequency, int symbolIndex) {
            this.frequency = frequency;
            this.symbolIndex = symbolIndex;
            this.left = null;
            this.right = null;
        }

        HuffmanNode(HuffmanNode left, HuffmanNode right) {
            this.frequency = left.frequency + right.frequency;
            this.symbolIndex = Math.min(left.symbolIndex, right.symbolIndex);
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    private static final Comparator<HuffmanNode> NODE_ORDER =
            Comparator.comparingInt((HuffmanNode node) -> node.frequency)
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

        new CodeCollector(codes).collect(buildTree(freq));
        return codes;
    }

    private static void validateInput(String s, int[] freq) {
        if (s == null || freq == null) {
            throw new IllegalArgumentException("Input string and frequency array must be non-null.");
        }
        if (s.length() != freq.length) {
            throw new IllegalArgumentException("String length and frequency array length must match.");
        }
        validateFrequencies(freq);
    }

    private static void validateFrequencies(int[] freq) {
        for (int i = 0; i < freq.length; i++) {
            if (freq[i] < 0) {
                throw new IllegalArgumentException("Frequency values must be non-negative: index " + i);
            }
        }
    }

    private static ArrayList<String> createEmptyCodeList(int size) {
        return new ArrayList<>(Collections.nCopies(size, ""));
    }

    private static HuffmanNode buildTree(int[] freq) {
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>(NODE_ORDER);
        for (int i = 0; i < freq.length; i++) {
            queue.add(new HuffmanNode(freq[i], i));
        }

        while (queue.size() > 1) {
            HuffmanNode left = queue.poll();
            HuffmanNode right = queue.poll();
            queue.add(new HuffmanNode(left, right));
        }

        return queue.peek();
    }

    private static final class CodeCollector {
        private final ArrayList<String> codes;
        private final StringBuilder path = new StringBuilder();

        CodeCollector(ArrayList<String> codes) {
            this.codes = codes;
        }

        void collect(HuffmanNode root) {
            visit(root);
        }

        private void visit(HuffmanNode node) {
            if (node == null) {
                return;
            }

            if (node.isLeaf()) {
                codes.set(node.symbolIndex, path.length() == 0 ? "0" : path.toString());
                return;
            }

            path.append(LEFT_BIT);
            visit(node.left);
            path.setLength(path.length() - 1);

            path.append(RIGHT_BIT);
            visit(node.right);
            path.setLength(path.length() - 1);
        }
    }
}
