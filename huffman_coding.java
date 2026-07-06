import java.util.*;

public class HuffmanCoding {

    private static class HuffmanNode {
        private final int frequency;
        private final int charIndex;
        private final HuffmanNode left;
        private final HuffmanNode right;

        HuffmanNode(int frequency, int charIndex) {
            this.frequency = frequency;
            this.charIndex = charIndex;
            this.left = null;
            this.right = null;
        }

        HuffmanNode(HuffmanNode left, HuffmanNode right) {
            this.frequency = left.frequency + right.frequency;
            this.charIndex = Math.min(left.charIndex, right.charIndex);
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    private static HuffmanNode buildTree(int[] freq) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(
            Comparator.comparingInt((HuffmanNode n) -> n.frequency)
                      .thenComparingInt(n -> n.charIndex)
        );
        for (int i = 0; i < freq.length; i++) {
            pq.add(new HuffmanNode(freq[i], i));
        }
        while (pq.size() >= 2) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            pq.add(new HuffmanNode(left, right));
        }
        return pq.peek();
    }

    private static ArrayList<String> collectCodes(HuffmanNode node, String prefix) {
        if (node == null) return new ArrayList<>();
        if (node.isLeaf()) {
            ArrayList<String> result = new ArrayList<>();
            result.add(prefix.isEmpty() ? "0" : prefix);
            return result;
        }
        ArrayList<String> codes = collectCodes(node.left, prefix + '0');
        codes.addAll(collectCodes(node.right, prefix + '1'));
        return codes;
    }

    static ArrayList<String> huffmanCodes(String s, int[] freq) {
        if (s.length() == 1) {
            return new ArrayList<>(Collections.singletonList("0"));
        }
        HuffmanNode root = buildTree(freq);
        return collectCodes(root, "");
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
