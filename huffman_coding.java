import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.function.BiConsumer;

class HuffmanCoding {

    private static final int INTERNAL_NODE_SYMBOL_INDEX = -1;
    private static final char LEFT_BRANCH_CODE = '0';
    private static final char RIGHT_BRANCH_CODE = '1';
    private static final String SINGLE_SYMBOL_CODE = String.valueOf(LEFT_BRANCH_CODE);

    private static final Comparator<Node> NODE_ORDER = Comparator
            .comparingLong((Node node) -> node.frequency)
            .thenComparingInt(node -> node.tieBreakIndex);

    private HuffmanCoding() {
    }

    private static final class Node {
        private final long frequency;
        private final int tieBreakIndex;
        private final int symbolIndex;
        private final Node left;
        private final Node right;

        private Node(int frequency, int symbolIndex) {
            this.frequency = frequency;
            this.tieBreakIndex = symbolIndex;
            this.symbolIndex = symbolIndex;
            this.left = null;
            this.right = null;
        }

        private Node(Node left, Node right) {
            this.frequency = left.frequency + right.frequency;
            this.tieBreakIndex = Math.min(left.tieBreakIndex, right.tieBreakIndex);
            this.symbolIndex = INTERNAL_NODE_SYMBOL_INDEX;
            this.left = left;
            this.right = right;
        }

        private boolean isLeaf() {
            return left == null && right == null;
        }
    }

    private static final class HuffmanTree {
        private final Node root;
        private final int symbolCount;

        private HuffmanTree(Node root, int symbolCount) {
            this.root = root;
            this.symbolCount = symbolCount;
        }

        private static HuffmanTree build(String symbols, int[] frequencies) {
            validateInput(symbols, frequencies);
            Node root = symbols.isEmpty() ? null : buildTree(frequencies);
            return new HuffmanTree(root, symbols.length());
        }

        private ArrayList<String> codesInTraversalOrder() {
            ArrayList<String> codes = new ArrayList<>(symbolCount);
            collectLeafCodes(root, new StringBuilder(), (leaf, code) -> codes.add(code));
            return codes;
        }

        private ArrayList<String> codesBySymbolOrder() {
            ArrayList<String> codes = createEmptyCodeList(symbolCount);
            collectLeafCodes(root, new StringBuilder(), (leaf, code) -> codes.set(leaf.symbolIndex, code));
            return codes;
        }

        private static Node buildTree(int[] frequencies) {
            PriorityQueue<Node> nodes = createLeafQueue(frequencies);

            while (nodes.size() > 1) {
                nodes.add(mergeLowestFrequencyNodes(nodes));
            }

            return nodes.poll();
        }

        private static PriorityQueue<Node> createLeafQueue(int[] frequencies) {
            PriorityQueue<Node> nodes = new PriorityQueue<>(NODE_ORDER);

            for (int i = 0; i < frequencies.length; i++) {
                nodes.add(new Node(frequencies[i], i));
            }

            return nodes;
        }

        private static Node mergeLowestFrequencyNodes(PriorityQueue<Node> nodes) {
            Node left = nodes.poll();
            Node right = nodes.poll();
            return new Node(left, right);
        }

        private static ArrayList<String> createEmptyCodeList(int size) {
            ArrayList<String> codes = new ArrayList<>(size);

            for (int i = 0; i < size; i++) {
                codes.add("");
            }

            return codes;
        }

        private static void collectLeafCodes(Node root, StringBuilder prefix, BiConsumer<Node, String> consumer) {
            if (root == null) {
                return;
            }

            if (root.isLeaf()) {
                consumer.accept(root, codeFor(prefix));
                return;
            }

            appendBranchCode(root.left, LEFT_BRANCH_CODE, prefix, consumer);
            appendBranchCode(root.right, RIGHT_BRANCH_CODE, prefix, consumer);
        }

        private static void appendBranchCode(Node node, char bit, StringBuilder prefix, BiConsumer<Node, String> consumer) {
            prefix.append(bit);
            collectLeafCodes(node, prefix, consumer);
            prefix.setLength(prefix.length() - 1);
        }

        private static String codeFor(StringBuilder prefix) {
            return prefix.length() == 0 ? SINGLE_SYMBOL_CODE : prefix.toString();
        }
    }

    static ArrayList<String> huffmanCodes(String symbols, int[] frequencies) {
        return HuffmanTree.build(symbols, frequencies).codesInTraversalOrder();
    }

    static ArrayList<String> huffmanCodesBySymbol(String symbols, int[] frequencies) {
        return HuffmanTree.build(symbols, frequencies).codesBySymbolOrder();
    }

    private static void validateInput(String symbols, int[] frequencies) {
        Objects.requireNonNull(symbols, "Input string must not be null");
        Objects.requireNonNull(frequencies, "Frequency array must not be null");

        if (symbols.length() != frequencies.length) {
            throw new IllegalArgumentException("Input string and frequency array must have the same length");
        }

        validateFrequencies(frequencies);
    }

    private static void validateFrequencies(int[] frequencies) {
        for (int frequency : frequencies) {
            if (frequency < 0) {
                throw new IllegalArgumentException("Frequencies must not be negative");
            }
        }
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
