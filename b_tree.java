import java.util.ArrayList;
import java.util.List;

/**
 * A B-tree: a self-balancing search tree in which every node holds between
 * {@code minDegree - 1} and {@code 2 * minDegree - 1} keys (the root may hold
 * fewer), keeping the tree shallow and search/insert costs logarithmic.
 *
 * <p>This implementation is generic over any {@link Comparable} key type and
 * supports insertion, membership queries, and an in-order traversal that
 * returns the keys in sorted order (decoupled from any form of output).
 *
 * @param <K> the key type, which must be mutually comparable
 */
class BTree<K extends Comparable<K>> {

    /** Minimum degree (often written {@code t}); governs node capacity. */
    private final int minDegree;

    /** Root of the tree, or {@code null} while the tree is empty. */
    private Node<K> root;

    /**
     * Creates an empty B-tree with the given minimum degree.
     *
     * @param minDegree the minimum degree {@code t}; must be at least 2
     * @throws IllegalArgumentException if {@code minDegree < 2}
     */
    BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("minDegree must be >= 2, was " + minDegree);
        }
        this.minDegree = minDegree;
        this.root = null;
    }

    /** Maximum number of keys a node may hold: {@code 2 * minDegree - 1}. */
    private int maxKeys() {
        return 2 * minDegree - 1;
    }

    /**
     * Inserts a key into the tree. Duplicate keys are permitted and stored
     * independently, matching classic B-tree behavior.
     *
     * @param key the key to insert
     */
    void insert(K key) {
        if (root == null) {
            root = new Node<>(minDegree, true);
            root.keys[0] = key;
            root.keyCount = 1;
            return;
        }

        if (root.keyCount == maxKeys()) {
            // Root is full: grow the tree one level by splitting into a new root.
            Node<K> newRoot = new Node<>(minDegree, false);
            newRoot.children[0] = root;
            newRoot.splitChild(0, root);

            int target = newRoot.keys[0].compareTo(key) < 0 ? 1 : 0;
            newRoot.children[target].insertNonFull(key);
            root = newRoot;
        } else {
            root.insertNonFull(key);
        }
    }

    /**
     * Reports whether the given key is present in the tree.
     *
     * @param key the key to look for
     * @return {@code true} if the key is present, {@code false} otherwise
     */
    boolean contains(K key) {
        return root != null && root.contains(key);
    }

    /**
     * Returns all keys in ascending (in-order) order.
     *
     * @return a newly allocated list of the keys in sorted order
     */
    List<K> toSortedList() {
        List<K> keys = new ArrayList<>();
        if (root != null) {
            root.collectInOrder(keys);
        }
        return keys;
    }

    /**
     * A single B-tree node, owning its keys and (for internal nodes) the
     * children that separate them.
     *
     * @param <K> the key type
     */
    private static final class Node<K extends Comparable<K>> {

        private final int minDegree;
        private final boolean leaf;
        private final K[] keys;
        private final Node<K>[] children;
        private int keyCount;

        @SuppressWarnings("unchecked")
        Node(int minDegree, boolean leaf) {
            this.minDegree = minDegree;
            this.leaf = leaf;
            this.keys = (K[]) new Comparable[2 * minDegree - 1];
            this.children = (Node<K>[]) new Node[2 * minDegree];
            this.keyCount = 0;
        }

        private int maxKeys() {
            return 2 * minDegree - 1;
        }

        /**
         * Inserts a key into the subtree rooted here, assuming this node is
         * not full (its caller guarantees space, splitting as needed).
         */
        void insertNonFull(K key) {
            int i = keyCount - 1;

            if (leaf) {
                // Shift larger keys right to open a slot, then drop the key in.
                while (i >= 0 && keys[i].compareTo(key) > 0) {
                    keys[i + 1] = keys[i];
                    i--;
                }
                keys[i + 1] = key;
                keyCount++;
                return;
            }

            // Find the child that should receive the key.
            while (i >= 0 && keys[i].compareTo(key) > 0) {
                i--;
            }
            int childIndex = i + 1;

            if (children[childIndex].keyCount == maxKeys()) {
                // The target child is full: split it before descending.
                splitChild(childIndex, children[childIndex]);
                if (keys[childIndex].compareTo(key) < 0) {
                    childIndex++;
                }
            }
            children[childIndex].insertNonFull(key);
        }

        /**
         * Splits {@code fullChild} (which must be full) into two nodes,
         * promoting its median key into this node at position {@code index}.
         *
         * @param index     position in this node to receive the median key
         * @param fullChild the full child to split
         */
        void splitChild(int index, Node<K> fullChild) {
            Node<K> newSibling = new Node<>(minDegree, fullChild.leaf);
            newSibling.keyCount = minDegree - 1;

            // Move the upper half of the keys into the new sibling.
            for (int j = 0; j < minDegree - 1; j++) {
                newSibling.keys[j] = fullChild.keys[j + minDegree];
            }
            if (!fullChild.leaf) {
                for (int j = 0; j < minDegree; j++) {
                    newSibling.children[j] = fullChild.children[j + minDegree];
                }
            }
            fullChild.keyCount = minDegree - 1;

            // Make room for the new sibling among this node's children.
            for (int j = keyCount; j > index; j--) {
                children[j + 1] = children[j];
            }
            children[index + 1] = newSibling;

            // Make room for, and store, the promoted median key.
            for (int j = keyCount - 1; j >= index; j--) {
                keys[j + 1] = keys[j];
            }
            keys[index] = fullChild.keys[minDegree - 1];
            keyCount++;
        }

        /** Reports whether the key exists in the subtree rooted here. */
        boolean contains(K key) {
            int i = 0;
            while (i < keyCount && key.compareTo(keys[i]) > 0) {
                i++;
            }
            if (i < keyCount && key.compareTo(keys[i]) == 0) {
                return true;
            }
            return !leaf && children[i].contains(key);
        }

        /** Appends every key in this subtree to {@code out} in ascending order. */
        void collectInOrder(List<K> out) {
            for (int i = 0; i < keyCount; i++) {
                if (!leaf) {
                    children[i].collectInOrder(out);
                }
                out.add(keys[i]);
            }
            if (!leaf) {
                children[keyCount].collectInOrder(out);
            }
        }
    }
}

/**
 * Demonstrates building a B-tree, traversing it in sorted order, and querying
 * for membership.
 */
class Main {
    public static void main(String[] args) {
        BTree<Integer> tree = new BTree<>(3);
        for (int key : new int[] {10, 20, 5, 6, 12, 30, 7, 17}) {
            tree.insert(key);
        }

        StringBuilder traversal = new StringBuilder("Traversal of the constructed tree is ");
        for (int key : tree.toSortedList()) {
            traversal.append(' ').append(key);
        }
        System.out.println(traversal);

        System.out.println(tree.contains(6) ? " | Present" : " | Not Present");
        System.out.println(tree.contains(15) ? " | Present" : " | Not Present");
    }
}
