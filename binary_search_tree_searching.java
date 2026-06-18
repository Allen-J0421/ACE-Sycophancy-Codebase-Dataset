class Node {
    int data;
    Node left, right;

    public Node(int item) {
        this.data = item;
        this.left = null;
        this.right = null;
    }
}
class BinarySearchTree {
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
