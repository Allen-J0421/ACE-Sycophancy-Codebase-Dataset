import java.util.ArrayDeque;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

class BinarySearchTree<T extends Comparable<T>> implements Iterable<T> {
    private Node<T> root;
    private int size;
    private int modCount;

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
            modCount++;
            return true;
        }
        Node<T> current = root;
        while (true) {
            int cmp = value.compareTo(current.data);
            if (cmp < 0) {
                if (current.left == null) { current.left = new Node<>(value); size++; modCount++; return true; }
                current = current.left;
            } else if (cmp > 0) {
                if (current.right == null) { current.right = new Node<>(value); size++; modCount++; return true; }
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
        return new InOrderIterator();
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

    private class InOrderIterator implements Iterator<T> {
        private final Deque<Node<T>> stack = new ArrayDeque<>();
        private final int expectedModCount = modCount;

        InOrderIterator() {
            pushLeft(root);
        }

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
            if (modCount != expectedModCount) throw new ConcurrentModificationException();
            if (!hasNext()) throw new NoSuchElementException();
            Node<T> node = stack.pop();
            pushLeft(node.right);
            return node.data;
        }
    }
}

class Main {
    public static void main(String[] args) {
        BinarySearchTree<Integer> intTree = new BinarySearchTree<>();
        check("isEmpty on new tree",   intTree.isEmpty());
        check("size is 0 on new tree", intTree.size() == 0);

        for (int value : new int[]{6, 2, 8, 7, 9}) intTree.insert(value);

        check("not empty after inserts",        !intTree.isEmpty());
        check("size is 5 after inserts",         intTree.size() == 5);
        check("contains 7 (present)",            intTree.contains(7));
        check("not contains 5 (absent)",        !intTree.contains(5));
        check("insert duplicate returns false", !intTree.insert(7));
        check("size unchanged after duplicate",  intTree.size() == 5);
        check("in-order traversal is sorted",    intTree.toString().equals("[2, 6, 7, 8, 9]"));

        try {
            Iterator<Integer> it = intTree.iterator();
            it.next();
            intTree.insert(99);
            it.next();
            check("concurrent modification detected", false);
        } catch (ConcurrentModificationException e) {
            check("concurrent modification detected", true);
        }

        BinarySearchTree<String> strTree = new BinarySearchTree<>();
        for (String s : new String[]{"mango", "apple", "orange", "banana"}) strTree.insert(s);

        check("string tree in-order sorted", strTree.toString().equals("[apple, banana, mango, orange]"));
        check("contains \"banana\"",          strTree.contains("banana"));
        check("not contains \"grape\"",      !strTree.contains("grape"));

        try {
            intTree.insert(null);
            check("insert(null) throws", false);
        } catch (IllegalArgumentException e) {
            check("insert(null) throws", true);
        }
    }

    private static void check(String label, boolean condition) {
        if (!condition) throw new AssertionError("FAIL: " + label);
        System.out.println("PASS: " + label);
    }
}
