class RedBlackTree {
    private static final char RED = 'R';
    private static final char BLACK = 'B';
    private static final int PRINT_INDENT = 10;

    public Node root;

    public RedBlackTree() {
        root = null;
    }

    class Node {
        int data;
        Node left;
        Node right;
        char colour;
        Node parent;

        Node(int data) {
            this.data = data;
            this.left = null;
            this.right = null;
            this.colour = RED;
            this.parent = null;
        }
    }

    Node rotateLeft(Node node) {
        if (node == null || node.right == null) {
            return node;
        }

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
        return pivot;
    }

    Node rotateRight(Node node) {
        if (node == null || node.left == null) {
            return node;
        }

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
        return pivot;
    }

    Node insertHelp(Node ignoredRoot, int data) {
        Node insertedNode = insertNode(data);
        fixAfterInsertion(insertedNode);
        return root;
    }

    public void insert(int data) {
        insertHelp(root, data);
    }

    private Node insertNode(int data) {
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

        Node insertedNode = new Node(data);
        insertedNode.parent = parent;

        if (parent == null) {
            root = insertedNode;
        } else if (data < parent.data) {
            parent.left = insertedNode;
        } else {
            parent.right = insertedNode;
        }

        return insertedNode;
    }

    private void fixAfterInsertion(Node node) {
        while (node != root && colourOf(parentOf(node)) == RED) {
            Node parent = parentOf(node);
            Node grandparent = parentOf(parent);

            if (parent == grandparent.left) {
                node = fixLeftSideViolation(node, parent, grandparent);
            } else {
                node = fixRightSideViolation(node, parent, grandparent);
            }
        }

        root.colour = BLACK;
        root.parent = null;
    }

    private Node fixLeftSideViolation(Node node, Node parent, Node grandparent) {
        Node uncle = grandparent.right;

        if (colourOf(uncle) == RED) {
            parent.colour = BLACK;
            uncle.colour = BLACK;
            grandparent.colour = RED;
            return grandparent;
        }

        if (node == parent.right) {
            node = parent;
            rotateLeft(node);
            parent = parentOf(node);
            grandparent = parentOf(parent);
        }

        parent.colour = BLACK;
        grandparent.colour = RED;
        rotateRight(grandparent);
        return node;
    }

    private Node fixRightSideViolation(Node node, Node parent, Node grandparent) {
        Node uncle = grandparent.left;

        if (colourOf(uncle) == RED) {
            parent.colour = BLACK;
            uncle.colour = BLACK;
            grandparent.colour = RED;
            return grandparent;
        }

        if (node == parent.left) {
            node = parent;
            rotateRight(node);
            parent = parentOf(node);
            grandparent = parentOf(parent);
        }

        parent.colour = BLACK;
        grandparent.colour = RED;
        rotateLeft(grandparent);
        return node;
    }

    private char colourOf(Node node) {
        return node == null ? BLACK : node.colour;
    }

    private Node parentOf(Node node) {
        return node == null ? null : node.parent;
    }

    void inorderTraversalHelper(Node node) {
        if (node == null) {
            return;
        }

        inorderTraversalHelper(node.left);
        System.out.printf("%d ", node.data);
        inorderTraversalHelper(node.right);
    }

    public void inorderTraversal() {
        inorderTraversalHelper(root);
    }

    void printTreeHelper(Node node, int space) {
        if (node == null) {
            return;
        }

        space += PRINT_INDENT;
        printTreeHelper(node.right, space);

        System.out.printf("\n");

        for (int i = PRINT_INDENT; i < space; i++) {
            System.out.printf(" ");
        }

        System.out.printf("%d", node.data);
        System.out.printf("\n");
        printTreeHelper(node.left, space);
    }

    public void printTree() {
        printTreeHelper(root, 0);
    }

    public static void main(String[] args) {
        RedBlackTree tree = new RedBlackTree();
        int[] values = {1, 4, 6, 3, 5, 7, 8, 2, 9};

        for (int value : values) {
            tree.insert(value);
            System.out.println();
            tree.inorderTraversal();
        }

        tree.printTree();
    }
}
