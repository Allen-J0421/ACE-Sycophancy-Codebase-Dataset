import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Builds optimal prefix-free (Huffman) codes for a set of symbols with known
 * frequencies.
 *
 * <p>The algorithm repeatedly merges the two lowest-frequency nodes until a
 * single tree remains, then assigns a {@code 0} bit to every left edge and a
 * {@code 1} bit to every right edge. The resulting leaf depths minimise the
 * total encoded length.
 *
 * <p>Ties are broken by the symbol's original insertion order so the output is
 * deterministic for a given input.
 */
public class HuffmanCoding {

    /** A node in the Huffman tree; immutable once constructed. */
    private static final class Node {
        final int frequency;
        /** Smallest original symbol index in this subtree; used for stable tie-breaking. */
        final int index;
        final Node left, right;

        /** Creates a leaf node for a single symbol. */
        Node(int frequency, int index) {
            this.frequency = frequency;
            this.index = index;
            this.left = null;
            this.right = null;
        }

        /** Creates an internal node by merging two subtrees. */
        Node(Node left, Node right) {
            this.frequency = left.frequency + right.frequency;
            this.index = Math.min(left.index, right.index);
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    /** Lower frequency first; break ties by original insertion order for determinism. */
    private static final Comparator<Node> BY_FREQUENCY =
            Comparator.comparingInt((Node n) -> n.frequency)
                      .thenComparingInt(n -> n.index);

    /**
     * Computes the Huffman code for each symbol.
     *
     * @param symbols     the distinct symbols, one per frequency
     * @param frequencies the frequency (weight) of each symbol; must be the same
     *                    length as {@code symbols} and contain no negative values
     * @return a map from each symbol to its binary code string, preserving the
     *         order in which symbols were supplied
     * @throws IllegalArgumentException if the inputs are null, mismatched in
     *                                  length, or contain a negative frequency
     */
    public static Map<Character, String> huffmanCodes(String symbols, int[] frequencies) {
        if (symbols == null || frequencies == null) {
            throw new IllegalArgumentException("symbols and frequencies must not be null");
        }
        if (symbols.length() != frequencies.length) {
            throw new IllegalArgumentException(
                    "symbols (" + symbols.length() + ") and frequencies ("
                            + frequencies.length + ") must have the same length");
        }

        int symbolCount = symbols.length();
        Map<Character, String> codes = new LinkedHashMap<>();
        if (symbolCount == 0) {
            return codes;
        }

        PriorityQueue<Node> queue = new PriorityQueue<>(BY_FREQUENCY);
        for (int i = 0; i < symbolCount; i++) {
            if (frequencies[i] < 0) {
                throw new IllegalArgumentException(
                        "frequency at index " + i + " is negative: " + frequencies[i]);
            }
            queue.add(new Node(frequencies[i], i));
        }

        // A single symbol has no edges to label, so it is assigned the code "0".
        if (symbolCount == 1) {
            codes.put(symbols.charAt(0), "0");
            return codes;
        }

        while (queue.size() >= 2) {
            Node smallest = queue.poll();
            Node secondSmallest = queue.poll();
            queue.add(new Node(smallest, secondSmallest));
        }

        assignCodes(queue.peek(), symbols, new StringBuilder(), codes);
        return codes;
    }

    /**
     * Walks the tree depth-first, accumulating the path bits and recording a code
     * for each leaf. {@code path} is mutated during traversal and restored on the
     * way back up to avoid allocating a new string at every edge.
     */
    private static void assignCodes(Node node, String symbols, StringBuilder path,
                                    Map<Character, String> codes) {
        if (node == null) {
            return;
        }
        if (node.isLeaf()) {
            codes.put(symbols.charAt(node.index), path.toString());
            return;
        }

        path.append('0');
        assignCodes(node.left, symbols, path, codes);
        path.setLength(path.length() - 1);

        path.append('1');
        assignCodes(node.right, symbols, path, codes);
        path.setLength(path.length() - 1);
    }

    public static void main(String[] args) {
        String symbols = "abcdef";
        int[] frequencies = {5, 9, 12, 13, 16, 45};

        Map<Character, String> codes = huffmanCodes(symbols, frequencies);
        codes.forEach((symbol, code) -> System.out.println(symbol + ": " + code));
    }
}
