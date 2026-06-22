class BTreeNode {
    private final int minDegree;
    private final int[] keys;
    private final BTreeNode[] children;
    private int keyCount;
    private final boolean leaf;

    BTreeNode(int minDegree, boolean leaf) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("minDegree must be at least 2");
        }
        this.minDegree = minDegree;
        this.leaf = leaf;
        this.keys = new int[2 * minDegree - 1];
        this.children = new BTreeNode[2 * minDegree];
    }

    boolean isFull() {
        return keyCount == keys.length;
    }

    private int findSearchIndex(int key) {
        int index = 0;
        while (index < keyCount && keys[index] < key) {
            index++;
        }
        return index;
    }

    private int findInsertionIndex(int key) {
        int index = 0;
        while (index < keyCount && keys[index] <= key) {
            index++;
        }
        return index;
    }

    private void insertKeyAt(int index, int key) {
        for (int i = keyCount - 1; i >= index; i--) {
            keys[i + 1] = keys[i];
        }
        keys[index] = key;
        keyCount++;
    }

    private void insertChildAt(int index, BTreeNode child) {
        for (int i = keyCount; i > index; i--) {
            children[i + 1] = children[i];
        }
        children[index + 1] = child;
    }

    void insertNonFull(int key) {
        if (leaf) {
            insertKeyAt(findInsertionIndex(key), key);
            return;
        }

        int childIndex = findInsertionIndex(key);
        if (children[childIndex].isFull()) {
            splitChild(childIndex, children[childIndex]);
            if (keys[childIndex] < key) {
                childIndex++;
            }
        }
        children[childIndex].insertNonFull(key);
    }

    void splitChild(int childIndex, BTreeNode fullChild) {
        BTreeNode sibling = new BTreeNode(minDegree, fullChild.leaf);
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

        children[childIndex] = fullChild;
        insertChildAt(childIndex, sibling);
        insertKeyAt(childIndex, fullChild.keys[minDegree - 1]);
    }

    void traverse() {
        System.out.print(traversalString());
    }

    String traversalString() {
        StringBuilder out = new StringBuilder();
        appendTraversal(out);
        return out.toString();
    }

    private void appendTraversal(StringBuilder out) {
        for (int i = 0; i < keyCount; i++) {
            if (!leaf) {
                children[i].appendTraversal(out);
            }
            out.append(" ").append(keys[i]);
        }
        if (!leaf) {
            children[keyCount].appendTraversal(out);
        }
    }

    BTreeNode search(int key) {
        int index = findSearchIndex(key);

        if (index < keyCount && keys[index] == key) {
            return this;
        }
        if (leaf) {
            return null;
        }
        return children[index].search(key);
    }
}

class BTree {
    private final int minDegree;
    private BTreeNode root;

    BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("minDegree must be at least 2");
        }
        this.minDegree = minDegree;
    }

    void traverse() {
        System.out.print(traversalString());
    }

    String traversalString() {
        return root == null ? "" : root.traversalString();
    }

    BTreeNode search(int key) {
        return root == null ? null : root.search(key);
    }

    boolean contains(int key) {
        return search(key) != null;
    }

    void insert(int key) {
        if (root == null) {
            root = new BTreeNode(minDegree, true);
            root.insertNonFull(key);
            return;
        }

        if (root.isFull()) {
            BTreeNode newRoot = new BTreeNode(minDegree, false);
            newRoot.splitChild(0, root);
            newRoot.insertNonFull(key);
            root = newRoot;
            return;
        }

        root.insertNonFull(key);
    }
}

class Main {
    public static void main(String[] args) {
        BTree tree = createDemoTree();
        printTraversal(tree);
        printSearchResult(tree, 6);
        printSearchResult(tree, 15);
    }

    private static BTree createDemoTree() {
        BTree tree = new BTree(3);
        int[] values = {10, 20, 5, 6, 12, 30, 7, 17};
        for (int value : values) {
            tree.insert(value);
        }
        return tree;
    }

    private static void printTraversal(BTree tree) {
        System.out.print("Traversal of the constructed tree is ");
        System.out.print(tree.traversalString());
        System.out.println();
    }

    private static void printSearchResult(BTree tree, int key) {
        if (tree.contains(key)) {
            System.out.println(" | Present");
        } else {
            System.out.println(" | Not Present");
        }
    }
}
