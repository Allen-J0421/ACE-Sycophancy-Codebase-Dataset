final class BinarySearchTree {
    private static final class Node {
        final int data;
        final Node left;
        final Node right;

        private Node(int item) {
            this(item, null, null);
        }

        private Node(int item, Node left, Node right) {
            this.data = item;
            this.left = left;
            this.right = right;
        }
    }

    private final Node root;

    private BinarySearchTree(Node root) {
        this.root = root;
    }

    boolean contains(int key) {
        return search(root, key);
    }

    private static boolean search(Node node, int key) {
        while (node != null) {
            if (node.data == key) {
                return true;
            }

            node = key > node.data ? node.right : node.left;
        }

        return false;
    }

    public static void main(String[] args) {
        BinarySearchTree tree = new BinarySearchTree(
            new Node(
                6,
                new Node(2),
                new Node(8, new Node(7), new Node(9))
            )
        );
        System.out.println(tree.contains(7));
    }
}
