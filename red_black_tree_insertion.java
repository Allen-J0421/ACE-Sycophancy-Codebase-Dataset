class RedBlackTree {
    private static final char RED = 'R';
    private static final char BLACK = 'B';
    private static final int PRINT_INDENT = 10;
    private static final int[] DEMO_VALUES = {1, 4, 6, 3, 5, 7, 8, 2, 9};

    public Node root;

    public RedBlackTree() {
        root = null;
    }

    private enum ChildSide {
        LEFT,
        RIGHT
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
        setParent(pivot.left, node);
        replaceParentChild(node, pivot);

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
        setParent(pivot.right, node);
        replaceParentChild(node, pivot);

        pivot.right = node;
        node.parent = pivot;
        return pivot;
    }

    private void replaceParentChild(Node oldChild, Node newChild) {
        Node parent = oldChild.parent;
        newChild.parent = parent;

        if (parent == null) {
            root = newChild;
        } else if (oldChild == parent.left) {
            parent.left = newChild;
        } else {
            parent.right = newChild;
        }
    }

    Node insertHelp(Node ignoredRoot, int data) {
        Node insertedNode = insertNode(data);
        fixAfterInsertion(insertedNode);
        return root;
    }

    public void insert(int data) {
        insertHelp(root, data);
    }

    public void insertAll(int[] values) {
        for (int value : values) {
            insert(value);
        }
    }

    private Node insertNode(int data) {
        Node parent = findInsertionParent(data);
        Node insertedNode = new Node(data);
        insertedNode.parent = parent;

        attachInsertedNode(parent, insertedNode);
        return insertedNode;
    }

    private Node findInsertionParent(int data) {
        Node parent = null;
        Node current = root;

        while (current != null) {
            parent = current;
            current = data < current.data ? current.left : current.right;
        }

        return parent;
    }

    private void attachInsertedNode(Node parent, Node insertedNode) {
        if (parent == null) {
            root = insertedNode;
        } else if (insertedNode.data < parent.data) {
            parent.left = insertedNode;
        } else {
            parent.right = insertedNode;
        }
    }

    private void fixAfterInsertion(Node node) {
        while (node != root && isRed(parentOf(node))) {
            Node parent = parentOf(node);
            Node grandparent = parentOf(parent);

            node = fixViolation(node, parent, grandparent, sideOf(parent));
        }

        root.colour = BLACK;
        root.parent = null;
    }

    private ChildSide sideOf(Node node) {
        if (node.parent != null && node == node.parent.left) {
            return ChildSide.LEFT;
        }

        return ChildSide.RIGHT;
    }

    private Node fixViolation(Node node, Node parent, Node grandparent, ChildSide parentSide) {
        Node uncle = parentSide == ChildSide.LEFT ? grandparent.right : grandparent.left;

        if (isRed(uncle)) {
            flipColours(parent, uncle, grandparent);
            return grandparent;
        }

        if (isTriangleCase(node, parent, parentSide)) {
            node = parent;
            rotateTowardParentSide(node, parentSide);
            parent = parentOf(node);
            grandparent = parentOf(parent);
        }

        parent.colour = BLACK;
        grandparent.colour = RED;
        rotateAwayFromParentSide(grandparent, parentSide);
        return node;
    }

    private boolean isTriangleCase(Node node, Node parent, ChildSide parentSide) {
        return parentSide == ChildSide.LEFT ? node == parent.right : node == parent.left;
    }

    private void rotateTowardParentSide(Node node, ChildSide parentSide) {
        if (parentSide == ChildSide.LEFT) {
            rotateLeft(node);
        } else {
            rotateRight(node);
        }
    }

    private void rotateAwayFromParentSide(Node node, ChildSide parentSide) {
        if (parentSide == ChildSide.LEFT) {
            rotateRight(node);
        } else {
            rotateLeft(node);
        }
    }

    private void flipColours(Node parent, Node uncle, Node grandparent) {
        parent.colour = BLACK;
        uncle.colour = BLACK;
        grandparent.colour = RED;
    }

    private boolean isRed(Node node) {
        return colourOf(node) == RED;
    }

    private char colourOf(Node node) {
        return node == null ? BLACK : node.colour;
    }

    private Node parentOf(Node node) {
        return node == null ? null : node.parent;
    }

    private void setParent(Node node, Node parent) {
        if (node != null) {
            node.parent = parent;
        }
    }

    void inorderTraversalHelper(Node node) {
        System.out.print(toInorderString(node));
    }

    public void inorderTraversal() {
        inorderTraversalHelper(root);
    }

    public String toInorderString() {
        return toInorderString(root);
    }

    private String toInorderString(Node node) {
        StringBuilder output = new StringBuilder();
        appendInorder(node, output);
        return output.toString();
    }

    private void appendInorder(Node node, StringBuilder output) {
        if (node == null) {
            return;
        }

        appendInorder(node.left, output);
        output.append(node.data).append(' ');
        appendInorder(node.right, output);
    }

    void printTreeHelper(Node node, int space) {
        StringBuilder output = new StringBuilder();
        appendTree(node, space, output);
        System.out.print(output);
    }

    public void printTree() {
        System.out.print(toTreeString());
    }

    public String toTreeString() {
        StringBuilder output = new StringBuilder();
        appendTree(root, 0, output);
        return output.toString();
    }

    private void appendTree(Node node, int space, StringBuilder output) {
        if (node == null) {
            return;
        }

        space += PRINT_INDENT;
        appendTree(node.right, space, output);

        output.append('\n');
        appendIndent(output, space);
        output.append(node.data).append('\n');
        appendTree(node.left, space, output);
    }

    private void appendIndent(StringBuilder output, int space) {
        for (int i = PRINT_INDENT; i < space; i++) {
            output.append(' ');
        }
    }

    public static void main(String[] args) {
        runDemo();
    }

    private static void runDemo() {
        RedBlackTree tree = new RedBlackTree();

        for (int value : DEMO_VALUES) {
            tree.insert(value);
            System.out.println();
            tree.inorderTraversal();
        }

        tree.printTree();
    }
}
