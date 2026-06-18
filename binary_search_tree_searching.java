import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

class BinarySearchTree<T extends Comparable<T>> implements Iterable<T> {
    private Node<T> root;
    private int size;

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public boolean insert(T value) {
        if (value == null) throw new IllegalArgumentException("null values are not permitted");
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
        if (key == null) throw new IllegalArgumentException("null values are not permitted");
        Node<T> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.data);
            if (cmp == 0) return true;
            current = cmp < 0 ? current.left : current.right;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private final Deque<Node<T>> stack = new ArrayDeque<>();

            { pushLeft(root); }

            private void pushLeft(Node<T> node) {
                while (node != null) {
                    stack.push(node);
                    node = node.left;
                }
            }

            @Override
            public boolean hasNext() {
                return !stack.isEmpty();
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                Node<T> node = stack.pop();
                pushLeft(node.right);
                return node.data;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (T item : this) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(item);
        }
        return sb.append("]").toString();
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
        for (int value : new int[]{6, 2, 8, 7, 9}) {
            intTree.insert(value);
        }
        System.out.println("in-order: " + intTree);           // [2, 6, 7, 8, 9]
        System.out.println("size:     " + intTree.size());    // 5
        System.out.println("contains(7): " + intTree.contains(7));       // true
        System.out.println("contains(5): " + intTree.contains(5));       // false
        System.out.println("insert(7) duplicate: " + intTree.insert(7)); // false

        BinarySearchTree<String> strTree = new BinarySearchTree<>();
        for (String s : new String[]{"mango", "apple", "orange", "banana"}) {
            strTree.insert(s);
        }
        System.out.println("in-order: " + strTree);                                 // [apple, banana, mango, orange]
        System.out.println("contains(\"banana\"): " + strTree.contains("banana")); // true
        System.out.println("contains(\"grape\"):  " + strTree.contains("grape"));  // false
    }
}
