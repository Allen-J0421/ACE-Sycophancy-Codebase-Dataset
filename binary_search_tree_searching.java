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

    private BinarySearchTree() {
    }

    static boolean search(Node root, int key) {
        while (root != null) {
            if (root.data == key) {
                return true;
            }

            root = key > root.data ? root.right : root.left;
        }

        return false;
    }

    private static Node createSampleTree() {
        return new Node(
            6,
            new Node(2),
            new Node(8, new Node(7), new Node(9))
        );
    }

    public static void main(String[] args) {
        Node root = createSampleTree();
        int key = 7;
        System.out.println(search(root, key));
    }
}
