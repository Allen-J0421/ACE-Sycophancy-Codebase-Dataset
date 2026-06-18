class BinarySearchTree<T extends Comparable<T>> {
    private Node<T> root;
    private int size;

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public boolean insert(T value) {
        if (root == null) {
            root = new Node<>(value);
            size++;
            return true;
        }
        Node<T> current = root;
        while (true) {
            int cmp = value.compareTo(current.data);
            if (cmp < 0) {
                if (current.left == null) { current.left = new Node<>(value); size++; return true; }
                current = current.left;
            } else if (cmp > 0) {
                if (current.right == null) { current.right = new Node<>(value); size++; return true; }
                current = current.right;
            } else {
                return false; // duplicate
            }
        }
    }

    public boolean contains(T key) {
        Node<T> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.data);
            if (cmp == 0) return true;
            current = cmp < 0 ? current.left : current.right;
        }
        return false;
    }

    private static class Node<T> {
        final T data;
        Node<T> left, right;

        Node(T data) {
            this.data = data;
        }
    }
}

class Main {
    public static void main(String[] args) {
        BinarySearchTree<Integer> intTree = new BinarySearchTree<>();
        System.out.println("isEmpty: " + intTree.isEmpty());  // true
        System.out.println("size:    " + intTree.size());     // 0

        for (int value : new int[]{6, 2, 8, 7, 9}) {
            intTree.insert(value);
        }

        System.out.println("isEmpty: " + intTree.isEmpty());             // false
        System.out.println("size:    " + intTree.size());                // 5
        System.out.println("contains(7): " + intTree.contains(7));      // true
        System.out.println("contains(5): " + intTree.contains(5));      // false
        System.out.println("insert(7) duplicate: " + intTree.insert(7)); // false
        System.out.println("size after duplicate: " + intTree.size());   // 5

        BinarySearchTree<String> strTree = new BinarySearchTree<>();
        for (String s : new String[]{"mango", "apple", "orange", "banana"}) {
            strTree.insert(s);
        }
        System.out.println("contains(\"banana\"): " + strTree.contains("banana")); // true
        System.out.println("contains(\"grape\"):  " + strTree.contains("grape"));  // false
        System.out.println("size: " + strTree.size());                              // 4
    }
}
