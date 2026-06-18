public final class BinarySearchTree {
    private static final int SEARCH_KEY = 7;
    private static final int[] SAMPLE_VALUES = {6, 2, 8, 7, 9};

    private Node root;

    void insert(int value) {
        root = insert(root, value);
    }

    void insertAll(int... values) {
        for (int value : values) {
            insert(value);
        }
    }

    boolean contains(int key) {
        return contains(root, key);
    }

    private static boolean contains(Node root, int key) {
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
        tree.insertAll(SAMPLE_VALUES);
        return tree;
    }

    public static void main(String[] args) {
        BinarySearchTree tree = createSampleTree();

        System.out.println(tree.contains(SEARCH_KEY));
    }

    private static final class Node {
        private final int value;
        private Node left;
        private Node right;

        private Node(int value) {
            this.value = value;
        }
    }
}
