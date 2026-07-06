import java.util.*;

interface HuffmanNodeVisitor {
    void visit(LeafNode leaf, String prefix);
    void visit(InternalNode node, String prefix);
}

sealed interface HuffmanNode permits LeafNode, InternalNode {
    int frequency();
    int charIndex();
    void accept(HuffmanNodeVisitor visitor, String prefix);
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

    public void accept(HuffmanNodeVisitor visitor, String prefix) {
        visitor.visit(this, prefix);
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
    public HuffmanNode left() { return left; }
    public HuffmanNode right() { return right; }

    public void accept(HuffmanNodeVisitor visitor, String prefix) {
        visitor.visit(this, prefix);
    }
}
