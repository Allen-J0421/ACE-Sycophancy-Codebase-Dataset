class BinarySearchTree {
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
        root = insert(root, value);
    }

    public boolean contains(int value) {
        Node current = root;

        while (current != null) {
            if (current.data == value) {
                return true;
            }

            current = value > current.data ? current.right : current.left;
        }

        return false;
    }

    public static BinarySearchTree fromValues(int... values) {
        BinarySearchTree tree = new BinarySearchTree();

        for (int value : values) {
            tree.insert(value);
        }

        return tree;
    }

    private Node insert(Node current, int value) {
        if (current == null) {
            return new Node(value);
        }

        if (value < current.data) {
            current.left = insert(current.left, value);
        } else if (value > current.data) {
            current.right = insert(current.right, value);
        }

        return current;
    }
}

class BinarySearchTreeSearching {
    public static void main(String[] args) {
        BinarySearchTree tree = BinarySearchTree.fromValues(6, 2, 8, 7, 9);
        int key = 7;

        System.out.println(tree.contains(key));
    }
}
