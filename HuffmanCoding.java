import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

public class HuffmanCoding {
    private static final String SINGLE_SYMBOL_CODE = "0";
    private static final String SAMPLE_SYMBOLS = "abcdef";
    private static final int[] SAMPLE_FREQUENCIES = {5, 9, 12, 13, 16, 45};
    private static final Comparator<Node> NODE_ORDER =
            Comparator.comparingInt((Node node) -> node.frequency)
                    .thenComparingInt(node -> node.sortOrder);

    private enum CodeOrder {
        TRAVERSAL,
        SYMBOL
    }

    private interface CodeAccumulator {
        void add(int symbolIndex, String code);

        ArrayList<String> toList();
    }

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

    private static final class TraversalOrderAccumulator implements CodeAccumulator {
        private final ArrayList<String> codes = new ArrayList<>();

        @Override
        public void add(int symbolIndex, String code) {
            codes.add(code);
        }

        @Override
        public ArrayList<String> toList() {
            return codes;
        }
    }

    private static final class SymbolOrderAccumulator implements CodeAccumulator {
        private final String[] codesBySymbol;

        private SymbolOrderAccumulator(int symbolCount) {
            codesBySymbol = new String[symbolCount];
        }

        @Override
        public void add(int symbolIndex, String code) {
            codesBySymbol[symbolIndex] = code;
        }

        @Override
        public ArrayList<String> toList() {
            return new ArrayList<>(Arrays.asList(codesBySymbol));
        }
    }

    static ArrayList<String> huffmanCodes(String symbols, int[] frequencies) {
        validateSymbols(symbols);
        validateFrequencyCount(symbols.length(), frequencies);
        return generateCodes(frequencies, symbols.length(), CodeOrder.TRAVERSAL);
    }

    static ArrayList<String> huffmanCodes(int[] frequencies) {
        validateFrequencies(frequencies);
        return generateCodes(frequencies, frequencies.length, CodeOrder.TRAVERSAL);
    }

    static ArrayList<String> huffmanCodesBySymbolOrder(String symbols, int[] frequencies) {
        validateSymbols(symbols);
        validateFrequencyCount(symbols.length(), frequencies);
        return generateCodes(frequencies, symbols.length(), CodeOrder.SYMBOL);
    }

    private static ArrayList<String> generateCodes(
            int[] frequencies,
            int symbolCount,
            CodeOrder codeOrder
    ) {
        if (symbolCount == 0) {
            return new ArrayList<>();
        }

        PriorityQueue<Node> queue = buildLeafQueue(frequencies);
        Node root = buildTree(queue);
        return buildCodes(root, createAccumulator(symbolCount, codeOrder));
    }

    private static void validateSymbols(String symbols) {
        Objects.requireNonNull(symbols, "symbols must not be null");
    }

    private static void validateFrequencies(int[] frequencies) {
        Objects.requireNonNull(frequencies, "frequencies must not be null");
        for (int frequency : frequencies) {
            if (frequency < 0) {
                throw new IllegalArgumentException("frequencies must be non-negative");
            }
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

    private static CodeAccumulator createAccumulator(int symbolCount, CodeOrder codeOrder) {
        if (codeOrder == CodeOrder.SYMBOL) {
            return new SymbolOrderAccumulator(symbolCount);
        }
        return new TraversalOrderAccumulator();
    }

    private static ArrayList<String> buildCodes(Node root, CodeAccumulator accumulator) {
        collectCodes(root, new StringBuilder(), accumulator);
        return accumulator.toList();
    }

    private static void collectCodes(Node node, StringBuilder path, CodeAccumulator accumulator) {
        if (node == null) {
            return;
        }

        if (node.isLeaf()) {
            accumulator.add(node.symbolIndex, path.length() == 0 ? SINGLE_SYMBOL_CODE : path.toString());
            return;
        }

        path.append('0');
        collectCodes(node.left, path, accumulator);
        path.deleteCharAt(path.length() - 1);

        path.append('1');
        collectCodes(node.right, path, accumulator);
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
