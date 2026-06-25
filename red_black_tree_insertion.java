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

    RedBlackTree() {
        this.root = null;
    }

    public void insert(int data) {
        Node inserted = bstInsert(data);
        fixAfterInsert(inserted);
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

            if (parent == grandparent.left) {
                Node uncle = grandparent.right;

                if (colorOf(uncle) == Color.RED) {
                    setColor(parent, Color.BLACK);
                    setColor(uncle, Color.BLACK);
                    setColor(grandparent, Color.RED);
                    node = grandparent;
                } else {
                    if (node == parent.right) {
                        node = parent;
                        rotateLeft(node);
                        parent = parentOf(node);
                        grandparent = parentOf(parent);
                    }

                    setColor(parent, Color.BLACK);
                    setColor(grandparent, Color.RED);
                    rotateRight(grandparent);
                }
            } else {
                Node uncle = grandparent.left;

                if (colorOf(uncle) == Color.RED) {
                    setColor(parent, Color.BLACK);
                    setColor(uncle, Color.BLACK);
                    setColor(grandparent, Color.RED);
                    node = grandparent;
                } else {
                    if (node == parent.left) {
                        node = parent;
                        rotateRight(node);
                        parent = parentOf(node);
                        grandparent = parentOf(parent);
                    }

                    setColor(parent, Color.BLACK);
                    setColor(grandparent, Color.RED);
                    rotateLeft(grandparent);
                }
            }
        }

        if (root != null) {
            root.color = Color.BLACK;
        }
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

    public void inorderTraversal() {
        inorderTraversal(root);
    }

    private void inorderTraversal(Node node) {
        if (node == null) {
            return;
        }

        inorderTraversal(node.left);
        System.out.printf("%d ", node.data);
        inorderTraversal(node.right);
    }

    public void printTree() {
        printTree(root, 0);
    }

    private void printTree(Node node, int indent) {
        if (node == null) {
            return;
        }

        int nextIndent = indent + 10;
        printTree(node.right, nextIndent);

        System.out.println();
        for (int i = 10; i < nextIndent; i++) {
            System.out.print(" ");
        }

        System.out.println(node.data + "(" + (node.color == Color.RED ? "R" : "B") + ")");
        printTree(node.left, nextIndent);
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
