final class Node {
    final int value;
    Node left;
    Node right;

    Node(int value) {
        this.value = value;
    }
}

final class BinarySearchTree {
    private BinarySearchTree() {
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

    private static Node createSampleTree() {
        Node root = new Node(6);
        root.left = new Node(2);
        root.right = new Node(8);
        root.right.left = new Node(7);
        root.right.right = new Node(9);

        return root;
    }

    public static void main(String[] args) {
        Node root = createSampleTree();
        int key = 7;

        System.out.println(search(root, key));
    }
}
