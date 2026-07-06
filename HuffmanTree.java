import java.util.*;

sealed interface HuffmanNode permits LeafNode, InternalNode {
    int frequency();
    int charIndex();
    void collectCodes(String prefix, Map<Character, String> codes, List<Character> chars);
}

final class LeafNode implements HuffmanNode {
    private final int frequency;
    private final int charIndex;

    LeafNode(int frequency, int charIndex) {
        this.frequency = frequency;
        this.charIndex = charIndex;
    }

    public int frequency() { return frequency; }
    public int charIndex() { return charIndex; }

    public void collectCodes(String prefix, Map<Character, String> codes, List<Character> chars) {
        codes.put(chars.get(charIndex), prefix.isEmpty() ? "0" : prefix);
    }
}

final class InternalNode implements HuffmanNode {
    private final int frequency;
    private final int charIndex;
    private final HuffmanNode left;
    private final HuffmanNode right;

    InternalNode(HuffmanNode left, HuffmanNode right) {
        this.frequency = left.frequency() + right.frequency();
        this.charIndex = Math.min(left.charIndex(), right.charIndex());
        this.left = left;
        this.right = right;
    }

    public int frequency() { return frequency; }
    public int charIndex() { return charIndex; }

    public void collectCodes(String prefix, Map<Character, String> codes, List<Character> chars) {
        left.collectCodes(prefix + '0', codes, chars);
        right.collectCodes(prefix + '1', codes, chars);
    }
}
