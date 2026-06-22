import java.util.ArrayList;
import java.util.List;

/**
 * A B-tree: a self-balancing search tree in which every node holds between
 * {@code minDegree - 1} and {@code 2 * minDegree - 1} keys (the root may hold
 * fewer), keeping the tree shallow and search/insert costs logarithmic.
 *
 * <p>This implementation is generic over any {@link Comparable} key type and
 * supports insertion, membership queries, and an in-order traversal that
 * returns the keys in sorted order (decoupled from any form of output). Nodes
 * store their keys and children in {@link ArrayList}s that grow as needed.
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

    /**
     * Inserts a key into the tree. Duplicate keys are permitted and stored
     * independently, matching classic B-tree behavior.
     *
     * @param key the key to insert
     */
    void insert(K key) {
        if (root == null) {
            root = new Node<>(minDegree, true);
            root.keys.add(key);
            return;
        }

        if (root.isFull()) {
            // Root is full: grow the tree one level by splitting into a new root.
            Node<K> newRoot = new Node<>(minDegree, false);
            newRoot.children.add(root);
            newRoot.splitChild(0, root);

            int target = newRoot.keys.get(0).compareTo(key) < 0 ? 1 : 0;
            newRoot.children.get(target).insertNonFull(key);
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
        private final List<K> keys;
        private final List<Node<K>> children;

        Node(int minDegree, boolean leaf) {
            this.minDegree = minDegree;
            this.leaf = leaf;
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
        }

        /** Maximum number of keys a node may hold: {@code 2 * minDegree - 1}. */
        private int maxKeys() {
            return 2 * minDegree - 1;
        }

        /** Reports whether this node is at key capacity and must be split. */
        private boolean isFull() {
            return keys.size() == maxKeys();
        }

        /**
         * Inserts a key into the subtree rooted here, assuming this node is
         * not full (its caller guarantees space, splitting as needed).
         */
        void insertNonFull(K key) {
            // Find the position of the first key greater than the new key.
            int i = keys.size() - 1;
            while (i >= 0 && keys.get(i).compareTo(key) > 0) {
                i--;
            }

            if (leaf) {
                // The list shifts later keys right for us.
                keys.add(i + 1, key);
                return;
            }

            int childIndex = i + 1;
            if (children.get(childIndex).isFull()) {
                // The target child is full: split it before descending.
                splitChild(childIndex, children.get(childIndex));
                if (keys.get(childIndex).compareTo(key) < 0) {
                    childIndex++;
                }
            }
            children.get(childIndex).insertNonFull(key);
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
            K median = fullChild.keys.get(minDegree - 1);

            // Hand the upper half of the keys to the new sibling, then drop
            // them (and the promoted median) from the original child.
            List<K> upperKeys = fullChild.keys.subList(minDegree, fullChild.keys.size());
            newSibling.keys.addAll(upperKeys);
            upperKeys.clear();
            fullChild.keys.remove(minDegree - 1);

            if (!fullChild.leaf) {
                List<Node<K>> upperChildren =
                        fullChild.children.subList(minDegree, fullChild.children.size());
                newSibling.children.addAll(upperChildren);
                upperChildren.clear();
            }

            // Splice the new sibling and the promoted median into this node.
            children.add(index + 1, newSibling);
            keys.add(index, median);
        }

        /** Reports whether the key exists in the subtree rooted here. */
        boolean contains(K key) {
            int i = 0;
            while (i < keys.size() && key.compareTo(keys.get(i)) > 0) {
                i++;
            }
            if (i < keys.size() && key.compareTo(keys.get(i)) == 0) {
                return true;
            }
            return !leaf && children.get(i).contains(key);
        }

        /** Appends every key in this subtree to {@code out} in ascending order. */
        void collectInOrder(List<K> out) {
            for (int i = 0; i < keys.size(); i++) {
                if (!leaf) {
                    children.get(i).collectInOrder(out);
                }
                out.add(keys.get(i));
            }
            if (!leaf) {
                children.get(keys.size()).collectInOrder(out);
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
