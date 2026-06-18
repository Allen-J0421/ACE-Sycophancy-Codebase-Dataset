class BinarySearchTree {
    private Node root;

    public boolean isEmpty() {
        return root == null;
    }

    public void insert(int value) {
        if (root == null) {
            root = new Node(value);
            return;
        }
        Node current = root;
        while (true) {
            if (value < current.data) {
                if (current.left == null) { current.left = new Node(value); return; }
                current = current.left;
            } else if (value > current.data) {
                if (current.right == null) { current.right = new Node(value); return; }
                current = current.right;
            } else {
                return; // duplicate — no-op
            }
        }
    }

    public boolean contains(int key) {
        Node current = root;
        while (current != null) {
            if (key == current.data) return true;
            current = key < current.data ? current.left : current.right;
        }
        return false;
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
        System.out.println("isEmpty before inserts: " + bst.isEmpty());  // true

        for (int value : new int[]{6, 2, 8, 7, 9}) {
            bst.insert(value);
        }

        System.out.println("isEmpty after inserts:  " + bst.isEmpty());  // false
        System.out.println("contains(7): " + bst.contains(7));           // true
        System.out.println("contains(5): " + bst.contains(5));           // false
        System.out.println("contains(6): " + bst.contains(6));           // true (root)
    }
}
