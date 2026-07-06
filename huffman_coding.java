import java.util.*;

class HuffmanEncoder {

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

    private final String characters;
    private final int[] frequencies;

    HuffmanEncoder(String characters, int[] frequencies) {
        this.characters = characters;
        this.frequencies = frequencies;
    }

    Map<Character, String> encode() {
        if (characters.length() == 1) {
            return Collections.singletonMap(characters.charAt(0), "0");
        }
        HuffmanNode root = buildTree();
        Map<Character, String> codes = new LinkedHashMap<>();
        collectCodes(root, "", codes);
        return codes;
    }

    private HuffmanNode buildTree() {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(
            Comparator.comparingInt((HuffmanNode n) -> n.frequency)
                      .thenComparingInt(n -> n.charIndex)
        );
        for (int i = 0; i < frequencies.length; i++) {
            pq.add(new HuffmanNode(frequencies[i], i));
        }
        while (pq.size() >= 2) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            pq.add(new HuffmanNode(left, right));
        }
        return pq.peek();
    }

    private void collectCodes(HuffmanNode node, String prefix, Map<Character, String> codes) {
        if (node == null) return;
        if (node.isLeaf()) {
            codes.put(characters.charAt(node.charIndex), prefix.isEmpty() ? "0" : prefix);
            return;
        }
        collectCodes(node.left, prefix + '0', codes);
        collectCodes(node.right, prefix + '1', codes);
    }
}

public class HuffmanCoding {

    public static void main(String[] args) {
        String s = "abcdef";
        int[] freq = {5, 9, 12, 13, 16, 45};

        Map<Character, String> codes = new HuffmanEncoder(s, freq).encode();
        for (Map.Entry<Character, String> entry : codes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
