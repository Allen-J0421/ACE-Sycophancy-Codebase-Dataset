class BinarySearchTree {
    private Node root;

    void insert(int value) {
        root = insert(root, value);
    }

    boolean contains(int key) {
        Node current = root;
        while (current != null) {
            if (key == current.data) return true;
            current = key < current.data ? current.left : current.right;
        }
        return false;
    }

    private Node insert(Node node, int value) {
        if (node == null) return new Node(value);
        if (value < node.data) node.left = insert(node.left, value);
        else if (value > node.data) node.right = insert(node.right, value);
        return node;
    }

    private static class Node {
        final int data;
        Node left, right;

        Node(int data) {
            this.data = data;
        }
    }

    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();
        for (int value : new int[]{6, 2, 8, 7, 9}) {
            bst.insert(value);
        }

        System.out.println(bst.contains(7));  // true
        System.out.println(bst.contains(5));  // false
        System.out.println(bst.contains(6));  // true (root)
    }
}
