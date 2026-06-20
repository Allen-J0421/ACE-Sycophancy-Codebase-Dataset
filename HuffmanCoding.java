import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class HuffmanCoding {
    private static final String SINGLE_SYMBOL_CODE = "0";
    private static final String SAMPLE_SYMBOLS = "abcdef";
    private static final int[] SAMPLE_FREQUENCIES = {5, 9, 12, 13, 16, 45};
    private static final Comparator<Node> NODE_ORDER =
            Comparator.comparingInt((Node node) -> node.frequency)
                    .thenComparingInt(node -> node.sortOrder);

    private static final class Node {
        private final int frequency;
        private final int sortOrder;
        private final int symbolIndex;
        private final Node left;
        private final Node right;

        private Node(int frequency, int symbolIndex) {
            this(frequency, symbolIndex, symbolIndex, null, null);
        }

        private Node(Node left, Node right) {
            this(
                    left.frequency + right.frequency,
                    Math.min(left.sortOrder, right.sortOrder),
                    -1,
                    left,
                    right
            );
        }

        private Node(int frequency, int sortOrder, int symbolIndex, Node left, Node right) {
            this.frequency = frequency;
            this.sortOrder = sortOrder;
            this.symbolIndex = symbolIndex;
            this.left = left;
            this.right = right;
        }

        private boolean isLeaf() {
            return left == null && right == null;
        }
    }

    static ArrayList<String> huffmanCodes(String symbols, int[] frequencies) {
        validateSymbols(symbols);
        validateFrequencyCount(symbols.length(), frequencies);
        return huffmanCodes(frequencies);
    }

    static ArrayList<String> huffmanCodes(int[] frequencies) {
        validateFrequencies(frequencies);
        if (frequencies.length == 0) {
            return new ArrayList<>();
        }

        PriorityQueue<Node> queue = buildLeafQueue(frequencies);
        Node root = buildTree(queue);
        return buildCodesInTraversalOrder(root);
    }

    static ArrayList<String> huffmanCodesBySymbolOrder(String symbols, int[] frequencies) {
        validateSymbols(symbols);
        validateFrequencyCount(symbols.length(), frequencies);
        if (frequencies.length == 0) {
            return new ArrayList<>();
        }

        PriorityQueue<Node> queue = buildLeafQueue(frequencies);
        Node root = buildTree(queue);
        return buildCodes(root, frequencies.length);
    }

    private static void validateSymbols(String symbols) {
        if (symbols == null) {
            throw new IllegalArgumentException("symbols must not be null");
        }
    }

    private static void validateFrequencies(int[] frequencies) {
        if (frequencies == null) {
            throw new IllegalArgumentException("frequencies must not be null");
        }
    }

    private static void validateFrequencyCount(int symbolCount, int[] frequencies) {
        validateFrequencies(frequencies);
        if (symbolCount != frequencies.length) {
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

    private static ArrayList<String> buildCodes(Node root, int symbolCount) {
        String[] codesBySymbol = new String[symbolCount];
        collectCodes(root, new StringBuilder(), codesBySymbol);
        return new ArrayList<>(Arrays.asList(codesBySymbol));
    }

    private static ArrayList<String> buildCodesInTraversalOrder(Node root) {
        ArrayList<String> codes = new ArrayList<>();
        collectCodes(root, new StringBuilder(), codes);
        return codes;
    }

    private static void collectCodes(Node node, StringBuilder path, String[] codesBySymbol) {
        if (node == null) {
            return;
        }

        if (node.isLeaf()) {
            codesBySymbol[node.symbolIndex] =
                    path.length() == 0 ? SINGLE_SYMBOL_CODE : path.toString();
            return;
        }

        path.append('0');
        collectCodes(node.left, path, codesBySymbol);
        path.deleteCharAt(path.length() - 1);

        path.append('1');
        collectCodes(node.right, path, codesBySymbol);
        path.deleteCharAt(path.length() - 1);
    }

    private static void collectCodes(Node node, StringBuilder path, List<String> codes) {
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
        System.out.print(String.join(" ", codes));
    }

    public static void main(String[] args) {
        ArrayList<String> codes = huffmanCodes(SAMPLE_SYMBOLS, SAMPLE_FREQUENCIES);
        printCodes(codes);
    }
}
