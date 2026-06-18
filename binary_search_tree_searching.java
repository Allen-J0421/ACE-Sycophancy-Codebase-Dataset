import java.util.ArrayDeque;
import java.util.ConcurrentModificationException;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringJoiner;

class BinarySearchTree<T extends Comparable<T>> implements Iterable<T> {
    private static final String NULL_NOT_PERMITTED = "null values are not permitted";

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
        if (value == null) throw new IllegalArgumentException(NULL_NOT_PERMITTED);
        Node<T> parent = null;
        Node<T> current = root;
        int cmp = 0;
        while (current != null) {
            cmp = value.compareTo(current.data);
            if (cmp == 0) return false; // duplicate
            parent = current;
            current = cmp < 0 ? current.left : current.right;
        }
        Node<T> node = new Node<>(value);
        if (parent == null) root = node;
        else if (cmp < 0) parent.left = node;
        else parent.right = node;
        size++;
        modCount++;
        return true;
    }

    public boolean contains(T key) {
        if (key == null) throw new IllegalArgumentException(NULL_NOT_PERMITTED);
        Node<T> current = root;
        while (current != null) {
            int cmp = key.compareTo(current.data);
            if (cmp == 0) return true;
            current = cmp < 0 ? current.left : current.right;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BinarySearchTree)) return false;
        BinarySearchTree<?> other = (BinarySearchTree<?>) o;
        if (size != other.size) return false;
        Iterator<T> it1 = iterator();
        Iterator<?> it2 = other.iterator();
        while (it1.hasNext()) {
            if (!it1.next().equals(it2.next())) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        for (T item : this) hash = 31 * hash + item.hashCode();
        return hash;
    }

    @Override
    public Iterator<T> iterator() {
        return new InOrderIterator();
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        for (T item : this) sj.add(String.valueOf(item));
        return sj.toString();
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
            BinarySearchTree<Integer> tmp = new BinarySearchTree<>();
            for (int value : new int[]{6, 2, 8, 7, 9}) tmp.insert(value);
            Iterator<Integer> it = tmp.iterator();
            it.next();
            tmp.insert(99);
            it.next();
            check("concurrent modification detected", false);
        } catch (ConcurrentModificationException e) {
            check("concurrent modification detected", true);
        }

        BinarySearchTree<Integer> sameElements = new BinarySearchTree<>();
        for (int value : new int[]{8, 9, 2, 7, 6}) sameElements.insert(value); // same values, different insertion order
        check("equal trees (same elements, different insertion order)", intTree.equals(sameElements));
        check("hashCode consistent with equals",                        intTree.hashCode() == sameElements.hashCode());
        check("reflexive equals",                                       intTree.equals(intTree));
        check("not equal to null",                                     !intTree.equals(null));

        BinarySearchTree<Integer> subset = new BinarySearchTree<>();
        for (int value : new int[]{6, 2, 8}) subset.insert(value);
        check("unequal trees (different sizes)", !intTree.equals(subset));

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
