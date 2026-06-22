package rbtree;

/**
 * A single node in a {@link RedBlackTree}.
 *
 * <p>Fields are package-private: they are shared mutable state owned by the
 * tree algorithm and the {@link TreePrinter}, both of which live in this
 * package. Nodes are created {@link Color#RED} by default, matching the
 * standard red-black insertion rule (a freshly inserted node is always red).
 */
class Node {
    int data;
    Node left;
    Node right;
    Node parent;
    Color color;

    Node(int data) {
        this.data = data;
        this.left = null;
        this.right = null;
        this.parent = null;
        this.color = Color.RED;
    }
}
