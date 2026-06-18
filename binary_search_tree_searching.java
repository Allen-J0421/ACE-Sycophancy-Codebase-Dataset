class BinarySearchTree {
    private Node root;
    private int size;

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public boolean insert(int value) {
        if (root == null) {
            root = new Node(value);
            size++;
            return true;
        }
        Node current = root;
        while (true) {
            if (value < current.data) {
                if (current.left == null) { current.left = new Node(value); size++; return true; }
                current = current.left;
            } else if (value > current.data) {
                if (current.right == null) { current.right = new Node(value); size++; return true; }
                current = current.right;
            } else {
                return false; // duplicate
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
}

class Main {
    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();
        System.out.println("isEmpty: " + bst.isEmpty());  // true
        System.out.println("size:    " + bst.size());     // 0

        for (int value : new int[]{6, 2, 8, 7, 9}) {
            bst.insert(value);
        }

        System.out.println("isEmpty: " + bst.isEmpty());        // false
        System.out.println("size:    " + bst.size());           // 5
        System.out.println("contains(7): " + bst.contains(7)); // true
        System.out.println("contains(5): " + bst.contains(5)); // false
        System.out.println("contains(6): " + bst.contains(6)); // true (root)

        System.out.println("insert(7) duplicate: " + bst.insert(7)); // false
        System.out.println("size after duplicate: " + bst.size());   // 5
    }
}
