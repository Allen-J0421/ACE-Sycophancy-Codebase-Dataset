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
     * Removes one occurrence of the given key from the tree, if present. When
     * duplicates exist, a single matching key is removed; absent keys are
     * ignored.
     *
     * @param key the key to remove
     */
    void delete(K key) {
        if (root == null) {
            return;
        }
        root.remove(key);

        // If the root lost its last key, shrink the tree by one level: its sole
        // child (if any) becomes the new root, otherwise the tree is now empty.
        if (root.keys.isEmpty()) {
            root = root.leaf ? null : root.children.get(0);
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

        /** Index of the first key not less than {@code key} (its lower bound). */
        private int lowerBound(K key) {
            int idx = 0;
            while (idx < keys.size() && keys.get(idx).compareTo(key) < 0) {
                idx++;
            }
            return idx;
        }

        /**
         * Removes {@code key} from the subtree rooted here. The caller
         * guarantees this node has at least {@code minDegree} keys (or is the
         * root), so removal never drives it below the minimum.
         */
        void remove(K key) {
            int idx = lowerBound(key);

            if (idx < keys.size() && keys.get(idx).compareTo(key) == 0) {
                if (leaf) {
                    keys.remove(idx);
                } else {
                    removeFromInternal(idx);
                }
                return;
            }

            if (leaf) {
                return; // Key is simply not present.
            }

            // The key, if present, lives in child[idx]. Whether that is the
            // node's last child matters after a possible merge below.
            boolean lastChild = (idx == keys.size());
            if (children.get(idx).keys.size() < minDegree) {
                refill(idx);
            }

            // A merge may have collapsed the last separator, so the key now
            // lives in the preceding child.
            int target = (lastChild && idx > keys.size()) ? idx - 1 : idx;
            children.get(target).remove(key);
        }

        /** Removes the key at {@code idx} from this internal node. */
        private void removeFromInternal(int idx) {
            K key = keys.get(idx);
            Node<K> leftChild = children.get(idx);
            Node<K> rightChild = children.get(idx + 1);

            if (leftChild.keys.size() >= minDegree) {
                // Replace with the in-order predecessor, then delete it below.
                K predecessor = maxOf(leftChild);
                keys.set(idx, predecessor);
                leftChild.remove(predecessor);
            } else if (rightChild.keys.size() >= minDegree) {
                // Replace with the in-order successor, then delete it below.
                K successor = minOf(rightChild);
                keys.set(idx, successor);
                rightChild.remove(successor);
            } else {
                // Both neighbours are minimal: merge them around the key and
                // delete from the merged child.
                merge(idx);
                leftChild.remove(key);
            }
        }

        /** Largest key in the subtree {@code node} (its rightmost leaf key). */
        private K maxOf(Node<K> node) {
            while (!node.leaf) {
                node = node.children.get(node.children.size() - 1);
            }
            return node.keys.get(node.keys.size() - 1);
        }

        /** Smallest key in the subtree {@code node} (its leftmost leaf key). */
        private K minOf(Node<K> node) {
            while (!node.leaf) {
                node = node.children.get(0);
            }
            return node.keys.get(0);
        }

        /**
         * Ensures {@code children[idx]} has at least {@code minDegree} keys by
         * borrowing from a sibling or merging when neither sibling can spare a
         * key.
         */
        private void refill(int idx) {
            if (idx != 0 && children.get(idx - 1).keys.size() >= minDegree) {
                borrowFromPrevious(idx);
            } else if (idx != keys.size() && children.get(idx + 1).keys.size() >= minDegree) {
                borrowFromNext(idx);
            } else if (idx != keys.size()) {
                merge(idx);
            } else {
                merge(idx - 1);
            }
        }

        /** Rotates a key from the left sibling through this node into child[idx]. */
        private void borrowFromPrevious(int idx) {
            Node<K> child = children.get(idx);
            Node<K> sibling = children.get(idx - 1);

            child.keys.add(0, keys.get(idx - 1));
            keys.set(idx - 1, sibling.keys.remove(sibling.keys.size() - 1));
            if (!child.leaf) {
                child.children.add(0, sibling.children.remove(sibling.children.size() - 1));
            }
        }

        /** Rotates a key from the right sibling through this node into child[idx]. */
        private void borrowFromNext(int idx) {
            Node<K> child = children.get(idx);
            Node<K> sibling = children.get(idx + 1);

            child.keys.add(keys.get(idx));
            keys.set(idx, sibling.keys.remove(0));
            if (!child.leaf) {
                child.children.add(sibling.children.remove(0));
            }
        }

        /**
         * Merges {@code children[idx + 1]} into {@code children[idx]}, pulling
         * the separating key at {@code idx} down between them.
         */
        private void merge(int idx) {
            Node<K> child = children.get(idx);
            Node<K> sibling = children.get(idx + 1);

            child.keys.add(keys.get(idx));
            child.keys.addAll(sibling.keys);
            if (!child.leaf) {
                child.children.addAll(sibling.children);
            }

            keys.remove(idx);
            children.remove(idx + 1);
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

        tree.delete(6);
        StringBuilder afterDelete = new StringBuilder("After deleting 6:");
        for (int key : tree.toSortedList()) {
            afterDelete.append(' ').append(key);
        }
        System.out.println(afterDelete);
        System.out.println(tree.contains(6) ? " | Present" : " | Not Present");
    }
}
