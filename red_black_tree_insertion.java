class RedBlackTree {
    private enum Color {
        RED,
        BLACK
    }

    private static final class Node {
        final int data;
        Color color;
        Node left;
        Node right;
        Node parent;

        Node(int data) {
            this.data = data;
            this.color = Color.RED;
        }
    }

    private Node root;
    private int size;

    RedBlackTree() {
        this.root = null;
        this.size = 0;
    }

    public void insert(int data) {
        Node inserted = bstInsert(data);
        fixAfterInsert(inserted);
        size++;
    }

    public boolean contains(int data) {
        Node current = root;

        while (current != null) {
            if (data == current.data) {
                return true;
            }

            current = data < current.data ? current.left : current.right;
        }

        return false;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return size;
    }

    public boolean isValidRedBlackTree() {
        if (root == null) {
            return true;
        }

        if (root.color != Color.BLACK) {
            return false;
        }

        return validateStructure(root, Long.MIN_VALUE, Long.MAX_VALUE) != -1;
    }

    private Node bstInsert(int data) {
        Node parent = null;
        Node current = root;

        while (current != null) {
            parent = current;
            if (data < current.data) {
                current = current.left;
            } else {
                current = current.right;
            }
        }

        Node inserted = new Node(data);
        inserted.parent = parent;

        if (parent == null) {
            root = inserted;
        } else if (data < parent.data) {
            parent.left = inserted;
        } else {
            parent.right = inserted;
        }

        return inserted;
    }

    private void fixAfterInsert(Node node) {
        while (node != root && colorOf(parentOf(node)) == Color.RED) {
            Node parent = parentOf(node);
            Node grandparent = parentOf(parent);

            if (isLeftChild(parent)) {
                node = fixAfterInsertLeftCase(node, parent, grandparent);
            } else {
                node = fixAfterInsertRightCase(node, parent, grandparent);
            }
        }

        if (root != null) {
            root.color = Color.BLACK;
        }
    }

    private Node fixAfterInsertLeftCase(Node node, Node parent, Node grandparent) {
        Node uncle = grandparent.right;

        if (colorOf(uncle) == Color.RED) {
            setColor(parent, Color.BLACK);
            setColor(uncle, Color.BLACK);
            setColor(grandparent, Color.RED);
            return grandparent;
        }

        if (node == parent.right) {
            rotateLeft(parent);
            node = parent;
            parent = parentOf(node);
        }

        setColor(parent, Color.BLACK);
        setColor(grandparent, Color.RED);
        rotateRight(grandparent);
        return parent;
    }

    private Node fixAfterInsertRightCase(Node node, Node parent, Node grandparent) {
        Node uncle = grandparent.left;

        if (colorOf(uncle) == Color.RED) {
            setColor(parent, Color.BLACK);
            setColor(uncle, Color.BLACK);
            setColor(grandparent, Color.RED);
            return grandparent;
        }

        if (node == parent.left) {
            rotateRight(parent);
            node = parent;
            parent = parentOf(node);
        }

        setColor(parent, Color.BLACK);
        setColor(grandparent, Color.RED);
        rotateLeft(grandparent);
        return parent;
    }

    private void rotateLeft(Node node) {
        Node pivot = node.right;
        node.right = pivot.left;

        if (pivot.left != null) {
            pivot.left.parent = node;
        }

        pivot.parent = node.parent;
        if (node.parent == null) {
            root = pivot;
        } else if (node == node.parent.left) {
            node.parent.left = pivot;
        } else {
            node.parent.right = pivot;
        }

        pivot.left = node;
        node.parent = pivot;
    }

    private void rotateRight(Node node) {
        Node pivot = node.left;
        node.left = pivot.right;

        if (pivot.right != null) {
            pivot.right.parent = node;
        }

        pivot.parent = node.parent;
        if (node.parent == null) {
            root = pivot;
        } else if (node == node.parent.right) {
            node.parent.right = pivot;
        } else {
            node.parent.left = pivot;
        }

        pivot.right = node;
        node.parent = pivot;
    }

    private static Color colorOf(Node node) {
        return node == null ? Color.BLACK : node.color;
    }

    private static void setColor(Node node, Color color) {
        if (node != null) {
            node.color = color;
        }
    }

    private static Node parentOf(Node node) {
        return node == null ? null : node.parent;
    }

    private static boolean isLeftChild(Node node) {
        return node != null && node.parent != null && node.parent.left == node;
    }

    private int validateStructure(Node node, long lowerBound, long upperBound) {
        if (node == null) {
            return 1;
        }

        if (node.data < lowerBound || node.data > upperBound) {
            return -1;
        }

        if (node.color == Color.RED) {
            if (colorOf(node.left) == Color.RED || colorOf(node.right) == Color.RED) {
                return -1;
            }
        }

        if (node.left != null && node.left.parent != node) {
            return -1;
        }
        if (node.right != null && node.right.parent != node) {
            return -1;
        }

        int leftBlackHeight = validateStructure(node.left, lowerBound, (long) node.data - 1);
        if (leftBlackHeight == -1) {
            return -1;
        }

        int rightBlackHeight = validateStructure(node.right, node.data, upperBound);
        if (rightBlackHeight == -1 || leftBlackHeight != rightBlackHeight) {
            return -1;
        }

        return leftBlackHeight + (node.color == Color.BLACK ? 1 : 0);
    }

    @Override
    public String toString() {
        return treeString();
    }

    public void inorderTraversal() {
        System.out.print(inorderString());
    }

    public String inorderString() {
        StringBuilder builder = new StringBuilder();
        inorderString(root, builder);
        return builder.toString();
    }

    private void inorderString(Node node, StringBuilder builder) {
        if (node == null) {
            return;
        }

        inorderString(node.left, builder);
        builder.append(node.data).append(' ');
        inorderString(node.right, builder);
    }

    public void printTree() {
        System.out.print(treeString());
    }

    public String treeString() {
        StringBuilder builder = new StringBuilder();
        treeString(root, 0, builder);
        return builder.toString();
    }

    private void treeString(Node node, int indent, StringBuilder builder) {
        if (node == null) {
            return;
        }

        int nextIndent = indent + 10;
        treeString(node.right, nextIndent, builder);

        builder.append('\n');
        for (int i = 10; i < nextIndent; i++) {
            builder.append(' ');
        }

        builder.append(node.data)
            .append('(')
            .append(node.color == Color.RED ? 'R' : 'B')
            .append(')')
            .append('\n');
        treeString(node.left, nextIndent, builder);
    }

    public static void main(String[] args) {
        RedBlackTree tree = new RedBlackTree();
        int[] values = {1, 4, 6, 3, 5, 7, 8, 2, 9};

        for (int value : values) {
            tree.insert(value);
            if (!tree.isValidRedBlackTree()) {
                throw new IllegalStateException("red-black tree invariant violation");
            }
            System.out.println();
            tree.inorderTraversal();
        }

        tree.printTree();
    }
}
