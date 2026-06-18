final class BinarySearchTree {
    private static final class Node {
        final int data;
        Node left;
        Node right;

        private Node(int item) {
            this.data = item;
        }
    }

    private Node root;

    BinarySearchTree() {
    }

    boolean contains(int key) {
        return findNode(root, key) != null;
    }

    void insert(int key) {
        root = insert(root, key);
    }

    private static Node findNode(Node node, int key) {
        while (node != null) {
            if (node.data == key) {
                return node;
            }

            node = key > node.data ? node.right : node.left;
        }

        return null;
    }

    private static Node insert(Node node, int key) {
        if (node == null) {
            return new Node(key);
        }

        if (key < node.data) {
            node.left = insert(node.left, key);
        } else if (key > node.data) {
            node.right = insert(node.right, key);
        }

        return node;
    }

    public static void main(String[] args) {
        BinarySearchTree tree = new BinarySearchTree();
        tree.insert(6);
        tree.insert(2);
        tree.insert(8);
        tree.insert(7);
        tree.insert(9);
        System.out.println(tree.contains(7));
    }
}
