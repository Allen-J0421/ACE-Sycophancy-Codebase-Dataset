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
        if (root == null) {
            root = new Node(key);
            return;
        }

        Node current = root;
        while (true) {
            if (key == current.data) {
                return;
            }

            if (key < current.data) {
                if (current.left == null) {
                    current.left = new Node(key);
                    return;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node(key);
                    return;
                }
                current = current.right;
            }
        }
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
