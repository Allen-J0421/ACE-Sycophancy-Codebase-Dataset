final class BinarySearchTree {
    private Node root;

    void insert(int value) {
        root = insert(root, value);
    }

    boolean contains(int key) {
        return search(root, key);
    }

    static boolean search(Node root, int key) {
        Node current = root;

        while (current != null) {
            if (current.value == key) {
                return true;
            }

            current = key > current.value ? current.right : current.left;
        }

        return false;
    }

    private static Node insert(Node node, int value) {
        if (node == null) {
            return new Node(value);
        }

        if (value < node.value) {
            node.left = insert(node.left, value);
        } else if (value > node.value) {
            node.right = insert(node.right, value);
        }

        return node;
    }

    private static BinarySearchTree createSampleTree() {
        BinarySearchTree tree = new BinarySearchTree();

        tree.insert(6);
        tree.insert(2);
        tree.insert(8);
        tree.insert(7);
        tree.insert(9);

        return tree;
    }

    public static void main(String[] args) {
        BinarySearchTree tree = createSampleTree();
        int key = 7;

        System.out.println(tree.contains(key));
    }
}

final class Node {
    final int value;
    Node left;
    Node right;

    Node(int value) {
        this.value = value;
    }
}
