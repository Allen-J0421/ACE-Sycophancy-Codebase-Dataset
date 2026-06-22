import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public final class HuffmanCoding {
    private static final char LEFT_BIT = '0';
    private static final char RIGHT_BIT = '1';

    private HuffmanCoding() {
        // Utility class.
    }

    private static final class HuffmanNode implements Comparable<HuffmanNode> {
        final int frequency;
        final int minSymbolIndex;
        final HuffmanNode left;
        final HuffmanNode right;

        HuffmanNode(int frequency, int minSymbolIndex) {
            this.frequency = frequency;
            this.minSymbolIndex = minSymbolIndex;
            this.left = null;
            this.right = null;
        }

        HuffmanNode(HuffmanNode left, HuffmanNode right) {
            this.frequency = left.frequency + right.frequency;
            this.minSymbolIndex = Math.min(left.minSymbolIndex, right.minSymbolIndex);
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }

        @Override
        public int compareTo(HuffmanNode other) {
            int byFrequency = Integer.compare(this.frequency, other.frequency);
            if (byFrequency != 0) {
                return byFrequency;
            }
            return Integer.compare(this.minSymbolIndex, other.minSymbolIndex);
        }
    }

    public static ArrayList<String> huffmanCodes(String symbols, int[] frequencies) {
        validateInput(symbols, frequencies);

        ArrayList<String> codes = createEmptyCodes(frequencies.length);
        if (frequencies.length <= 1) {
            if (frequencies.length == 1) {
                codes.set(0, "0");
            }
            return codes;
        }

        new CodeCollector(codes).collectCodes(buildTree(frequencies));
        return codes;
    }

    private static void validateInput(String symbols, int[] frequencies) {
        if (symbols == null || frequencies == null) {
            throw new IllegalArgumentException("Input string and frequency array must be non-null.");
        }
        if (symbols.length() != frequencies.length) {
            throw new IllegalArgumentException("String length and frequency array length must match.");
        }
        validateFrequencies(frequencies);
    }

    private static void validateFrequencies(int[] frequencies) {
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] < 0) {
                throw new IllegalArgumentException("Frequency values must be non-negative: index " + i);
            }
        }
    }

    private static ArrayList<String> createEmptyCodes(int size) {
        return new ArrayList<>(Collections.nCopies(size, ""));
    }

    private static HuffmanNode buildTree(int[] frequencies) {
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>();
        for (int i = 0; i < frequencies.length; i++) {
            queue.add(new HuffmanNode(frequencies[i], i));
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

        void collectCodes(HuffmanNode root) {
            traverse(root);
        }

        private void traverse(HuffmanNode node) {
            if (node == null) {
                return;
            }

            if (node.isLeaf()) {
                codes.set(node.minSymbolIndex, path.length() == 0 ? "0" : path.toString());
                return;
            }

            path.append(LEFT_BIT);
            traverse(node.left);
            path.setLength(path.length() - 1);

            path.append(RIGHT_BIT);
            traverse(node.right);
            path.setLength(path.length() - 1);
        }
    }
}
