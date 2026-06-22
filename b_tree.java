final class BTree {
    private static final class Node {
        private final int minDegree;
        private final int[] keys;
        private final Node[] children;
        private int keyCount;
        private final boolean leaf;

        private Node(int minDegree, boolean leaf) {
            if (minDegree < 2) {
                throw new IllegalArgumentException("minDegree must be at least 2");
            }
            this.minDegree = minDegree;
            this.leaf = leaf;
            this.keys = new int[2 * minDegree - 1];
            this.children = new Node[2 * minDegree];
            this.keyCount = 0;
        }

        private boolean isFull() {
            return keyCount == keys.length;
        }

        private int findKeyIndex(int key) {
            int index = 0;
            while (index < keyCount && keys[index] < key) {
                index++;
            }
            return index;
        }

        private void insertNonFull(int key) {
            int index = keyCount - 1;

            if (leaf) {
                while (index >= 0 && keys[index] > key) {
                    keys[index + 1] = keys[index];
                    index--;
                }
                keys[index + 1] = key;
                keyCount++;
                return;
            }

            while (index >= 0 && keys[index] > key) {
                index--;
            }

            int childIndex = index + 1;
            if (children[childIndex].isFull()) {
                splitChild(childIndex);
                if (keys[childIndex] < key) {
                    childIndex++;
                }
            }
            children[childIndex].insertNonFull(key);
        }

        private void splitChild(int childIndex) {
            Node fullChild = children[childIndex];
            Node sibling = new Node(minDegree, fullChild.leaf);
            sibling.keyCount = minDegree - 1;

            for (int i = 0; i < minDegree - 1; i++) {
                sibling.keys[i] = fullChild.keys[i + minDegree];
            }

            if (!fullChild.leaf) {
                for (int i = 0; i < minDegree; i++) {
                    sibling.children[i] = fullChild.children[i + minDegree];
                }
            }

            fullChild.keyCount = minDegree - 1;

            for (int i = keyCount; i > childIndex; i--) {
                children[i + 1] = children[i];
            }
            children[childIndex + 1] = sibling;

            for (int i = keyCount - 1; i >= childIndex; i--) {
                keys[i + 1] = keys[i];
            }
            keys[childIndex] = fullChild.keys[minDegree - 1];
            keyCount++;
        }

        private void traverse() {
            for (int i = 0; i < keyCount; i++) {
                if (!leaf) {
                    children[i].traverse();
                }
                System.out.print(" " + keys[i]);
            }
            if (!leaf) {
                children[keyCount].traverse();
            }
        }

        private Node search(int key) {
            int index = findKeyIndex(key);

            if (index < keyCount && keys[index] == key) {
                return this;
            }
            if (leaf) {
                return null;
            }
            return children[index].search(key);
        }
    }

    private final int minDegree;
    private Node root;

    BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("minDegree must be at least 2");
        }
        this.minDegree = minDegree;
    }

    void traverse() {
        if (root != null) {
            root.traverse();
        }
    }

    Node search(int key) {
        return root == null ? null : root.search(key);
    }

    void insert(int key) {
        if (root == null) {
            root = new Node(minDegree, true);
            root.keys[0] = key;
            root.keyCount = 1;
            return;
        }

        if (root.isFull()) {
            Node newRoot = new Node(minDegree, false);
            newRoot.children[0] = root;
            newRoot.splitChild(0);

            int childIndex = newRoot.keys[0] < key ? 1 : 0;
            newRoot.children[childIndex].insertNonFull(key);
            root = newRoot;
            return;
        }

        root.insertNonFull(key);
    }
}

class Main {
    public static void main(String[] args) {
        BTree tree = new BTree(3);
        tree.insert(10);
        tree.insert(20);
        tree.insert(5);
        tree.insert(6);
        tree.insert(12);
        tree.insert(30);
        tree.insert(7);
        tree.insert(17);

        System.out.print("Traversal of the constructed tree is ");
        tree.traverse();
        System.out.println();

        int key = 6;
        if (tree.search(key) != null) {
            System.out.println(" | Present");
        } else {
            System.out.println(" | Not Present");
        }

        key = 15;
        if (tree.search(key) != null) {
            System.out.println(" | Present");
        } else {
            System.out.println(" | Not Present");
        }
    }
}
