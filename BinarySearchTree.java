public final class BinarySearchTree {
    private static final int SEARCH_KEY = 7;
    private static final int[] SAMPLE_VALUES = {6, 2, 8, 7, 9};

    private Node root;

    public BinarySearchTree insert(int value) {
        if (root == null) {
            root = new Node(value);
            return this;
        }

        Node current = root;

        while (true) {
            if (value == current.value) {
                return this;
            }

            if (value < current.value) {
                if (current.left == null) {
                    current.left = new Node(value);
                    return this;
                }

                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node(value);
                    return this;
                }

                current = current.right;
            }
        }
    }

    public BinarySearchTree insertAll(int... values) {
        for (int value : values) {
            insert(value);
        }

        return this;
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

    public static void main(String[] args) {
        BinarySearchTree tree = new BinarySearchTree().insertAll(SAMPLE_VALUES);

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
