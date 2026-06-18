public final class BinarySearchTree {
    private static final int SEARCH_KEY = 7;
    private static final int[] SAMPLE_VALUES = {6, 2, 8, 7, 9};

    private Node root;

    public void insert(int value) {
        if (root == null) {
            root = new Node(value);
            return;
        }

        Node current = root;

        while (true) {
            if (value == current.value) {
                return;
            }

            if (value < current.value) {
                if (current.left == null) {
                    current.left = new Node(value);
                    return;
                }

                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node(value);
                    return;
                }

                current = current.right;
            }
        }
    }

    public void insertAll(int... values) {
        for (int value : values) {
            insert(value);
        }
    }

    public boolean contains(int key) {
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
