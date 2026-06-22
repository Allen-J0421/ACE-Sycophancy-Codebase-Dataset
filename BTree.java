import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

final class BTree<T extends Comparable<? super T>> {
    private static final class Node<T extends Comparable<? super T>> {
        private final int minDegree;
        private final Object[] keys;
        private final Node<T>[] children;
        private int keyCount;
        private final boolean leaf;

        @SuppressWarnings("unchecked")
        private Node(int minDegree, boolean leaf) {
            if (minDegree < 2) {
                throw new IllegalArgumentException("minDegree must be at least 2");
            }
            this.minDegree = minDegree;
            this.leaf = leaf;
            this.keys = new Object[2 * minDegree - 1];
            this.children = (Node<T>[]) new Node[2 * minDegree];
        }

        private boolean isFull() {
            return keyCount == keys.length;
        }

        @SuppressWarnings("unchecked")
        private T keyAt(int index) {
            return (T) keys[index];
        }

        private int compareKey(T left, T right) {
            return left.compareTo(right);
        }

        private int findSearchIndex(T key) {
            int index = 0;
            while (index < keyCount && compareKey(keyAt(index), key) < 0) {
                index++;
            }
            return index;
        }

        private int findInsertionIndex(T key) {
            int index = 0;
            while (index < keyCount && compareKey(keyAt(index), key) <= 0) {
                index++;
            }
            return index;
        }

        private void insertKeyAt(int index, T key) {
            for (int i = keyCount - 1; i >= index; i--) {
                keys[i + 1] = keys[i];
            }
            keys[index] = key;
            keyCount++;
        }

        private void insertChildAt(int index, Node<T> child) {
            for (int i = keyCount; i > index; i--) {
                children[i + 1] = children[i];
            }
            children[index + 1] = child;
        }

        private void insertNonFull(T key) {
            if (leaf) {
                insertKeyAt(findInsertionIndex(key), key);
                return;
            }

            int childIndex = findInsertionIndex(key);
            if (children[childIndex].isFull()) {
                splitChild(childIndex);
                if (compareKey(keyAt(childIndex), key) < 0) {
                    childIndex++;
                }
            }
            children[childIndex].insertNonFull(key);
        }

        private void splitChild(int childIndex) {
            Node<T> fullChild = children[childIndex];
            Node<T> sibling = new Node<>(minDegree, fullChild.leaf);
            sibling.keyCount = minDegree - 1;

            for (int i = 0; i < minDegree - 1; i++) {
                sibling.keys[i] = fullChild.keys[i + minDegree];
            }

            if (!fullChild.leaf) {
                for (int i = 0; i < minDegree; i++) {
                    sibling.children[i] = fullChild.children[i + minDegree];
                }
            }

            T middleKey = fullChild.keyAt(minDegree - 1);
            fullChild.keyCount = minDegree - 1;

            children[childIndex] = fullChild;
            insertChildAt(childIndex, sibling);
            insertKeyAt(childIndex, middleKey);
        }

        private boolean contains(T key) {
            int index = findSearchIndex(key);

            if (index < keyCount && compareKey(keyAt(index), key) == 0) {
                return true;
            }
            if (leaf) {
                return false;
            }
            return children[index].contains(key);
        }

        private List<T> keysInOrder() {
            List<T> output = new ArrayList<>();
            forEachKeyInOrder(output::add);
            return output;
        }

        private void forEachKeyInOrder(Consumer<T> consumer) {
            for (int i = 0; i < keyCount; i++) {
                if (!leaf) {
                    children[i].forEachKeyInOrder(consumer);
                }
                consumer.accept(keyAt(i));
            }
            if (!leaf) {
                children[keyCount].forEachKeyInOrder(consumer);
            }
        }
    }

    private final int minDegree;
    private Node<T> root;

    BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("minDegree must be at least 2");
        }
        this.minDegree = minDegree;
    }

    List<T> keysInOrder() {
        return root == null ? new ArrayList<>() : root.keysInOrder();
    }

    boolean contains(T key) {
        return root != null && root.contains(key);
    }

    void insert(T key) {
        if (root == null) {
            root = new Node<>(minDegree, true);
            root.insertNonFull(key);
            return;
        }

        if (root.isFull()) {
            Node<T> newRoot = new Node<>(minDegree, false);
            newRoot.children[0] = root;
            newRoot.splitChild(0);
            newRoot.insertNonFull(key);
            root = newRoot;
            return;
        }

        root.insertNonFull(key);
    }
}
