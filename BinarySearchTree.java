public final class BinarySearchTree {
    private static final class Node {
        private final int data;
        private Node left;
        private Node right;

        private Node(int data) {
            this.data = data;
        }
    }

    private Node root;

    public void insert(int value) {
        if (root == null) {
            root = new Node(value);
            return;
        }

        Node current = root;

        while (true) {
            if (value < current.data) {
                if (current.left == null) {
                    current.left = new Node(value);
                    return;
                }

                current = current.left;
            } else if (value > current.data) {
                if (current.right == null) {
                    current.right = new Node(value);
                    return;
                }

                current = current.right;
            } else {
                return;
            }
        }
    }

    public void insertAll(int... values) {
        for (int value : values) {
            insert(value);
        }
    }

    public boolean contains(int value) {
        return findNode(value) != null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public static BinarySearchTree fromValues(int... values) {
        BinarySearchTree tree = new BinarySearchTree();
        tree.insertAll(values);
        return tree;
    }

    private Node findNode(int value) {
        Node current = root;

        while (current != null) {
            if (current.data == value) {
                return current;
            }

            current = value > current.data ? current.right : current.left;
        }

        return null;
    }
}
