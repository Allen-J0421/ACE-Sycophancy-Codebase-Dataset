class BTreeNode {
    int[] keys;
    int t;
    BTreeNode[] C;
    int n;
    boolean leaf;

    public BTreeNode(int t, boolean leaf) {
        this.keys = new int[2 * t - 1];
        this.t = t;
        this.C = new BTreeNode[2 * t];
        this.n = 0;
        this.leaf = leaf;
    }

    boolean isFull() {
        return n == maxKeys();
    }

    private int maxKeys() {
        return 2 * t - 1;
    }

    private int insertionPosition(int k) {
        int i = n - 1;
        while (i >= 0 && keys[i] > k) {
            i--;
        }
        return i + 1;
    }

    void insertNonFull(int k) {
        if (leaf) {
            insertIntoLeaf(k);
            return;
        }

        insertIntoChild(k);
    }

    private void insertIntoLeaf(int k) {
        insertKeyAt(insertionPosition(k), k);
    }

    private void insertIntoChild(int k) {
        int childIndex = insertionPosition(k);
        childIndex = prepareChildForInsert(childIndex, k);
        C[childIndex].insertNonFull(k);
    }

    private int prepareChildForInsert(int childIndex, int k) {
        if (!C[childIndex].isFull()) {
            return childIndex;
        }

        splitChild(childIndex, C[childIndex]);
        return keys[childIndex] < k ? childIndex + 1 : childIndex;
    }

    void splitChild(int i, BTreeNode y) {
        BTreeNode z = createRightSibling(y);
        int medianKey = y.keys[t - 1];
        y.n = t - 1;
        shiftChildrenRight(i + 1);
        C[i + 1] = z;
        insertKeyAt(i, medianKey);
    }

    private BTreeNode createRightSibling(BTreeNode node) {
        BTreeNode sibling = new BTreeNode(node.t, node.leaf);
        sibling.n = t - 1;
        System.arraycopy(node.keys, t, sibling.keys, 0, t - 1);
        if (!node.leaf) {
            System.arraycopy(node.C, t, sibling.C, 0, t);
        }
        return sibling;
    }

    private void insertKeyAt(int index, int key) {
        shiftKeysRight(index);
        keys[index] = key;
        n++;
    }

    private void shiftKeysRight(int fromIndex) {
        System.arraycopy(keys, fromIndex, keys, fromIndex + 1, n - fromIndex);
    }

    private void shiftChildrenRight(int fromIndex) {
        System.arraycopy(C, fromIndex, C, fromIndex + 1, n - fromIndex + 1);
    }

    void traverse() {
        for (int i = 0; i < n; i++) {
            if (!leaf) {
                C[i].traverse();
            }
            System.out.print(" " + keys[i]);
        }
        if (!leaf) {
            C[n].traverse();
        }
    }

    BTreeNode search(int k) {
        int i = searchPosition(k);
        if (i < n && k == keys[i]) {
            return this;
        }
        if (leaf) {
            return null;
        }
        return C[i].search(k);
    }

    private int searchPosition(int k) {
        int i = 0;
        while (i < n && k > keys[i]) {
            i++;
        }
        return i;
    }
}

class BTree {
    BTreeNode root;
    int t;

    public BTree(int t) {
        this.root = null;
        this.t = t;
    }

    void traverse() {
        if (root != null) {
            root.traverse();
        }
    }

    BTreeNode search(int k) {
        return root == null ? null : root.search(k);
    }

    boolean contains(int k) {
        return search(k) != null;
    }

    void insert(int k) {
        if (root == null) {
            createRoot(k);
            return;
        }

        if (root.isFull()) {
            splitRootAndInsert(k);
            return;
        }

        root.insertNonFull(k);
    }

    private void createRoot(int k) {
        root = new BTreeNode(t, true);
        root.keys[0] = k;
        root.n = 1;
    }

    private void splitRootAndInsert(int k) {
        BTreeNode newRoot = new BTreeNode(t, false);
        newRoot.C[0] = root;
        newRoot.splitChild(0, root);
        newRoot.C[rootChildIndexFor(newRoot, k)].insertNonFull(k);
        root = newRoot;
    }

    private int rootChildIndexFor(BTreeNode newRoot, int k) {
        return newRoot.keys[0] < k ? 1 : 0;
    }
}

class Main {
    private static final int MINIMUM_DEGREE = 3;
    private static final int[] SAMPLE_VALUES = {10, 20, 5, 6, 12, 30, 7, 17};
    private static final int[] SEARCH_KEYS = {6, 15};

    public static void main(String[] args) {
        BTree tree = buildSampleTree();

        System.out.print("Traversal of the constructed tree is ");
        tree.traverse();
        System.out.println();

        for (int key : SEARCH_KEYS) {
            printSearchResult(tree, key);
        }
    }

    private static BTree buildSampleTree() {
        BTree tree = new BTree(MINIMUM_DEGREE);
        for (int value : SAMPLE_VALUES) {
            tree.insert(value);
        }
        return tree;
    }

    private static void printSearchResult(BTree tree, int key) {
        if (tree.contains(key)) {
            System.out.println(" | Present");
        } else {
            System.out.println(" | Not Present");
        }
    }
}
