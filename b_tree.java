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
            int position = insertionPosition(k);
            shiftKeysRight(position);
            keys[position] = k;
            n++;
        } else {
            int childIndex = insertionPosition(k);
            if (C[childIndex].isFull()) {
                splitChild(childIndex, C[childIndex]);
                if (keys[childIndex] < k) {
                    childIndex++;
                }
            }
            C[childIndex].insertNonFull(k);
        }
    }

    void splitChild(int i, BTreeNode y) {
        BTreeNode z = new BTreeNode(y.t, y.leaf);
        z.n = t - 1;
        System.arraycopy(y.keys, t, z.keys, 0, t - 1);
        if (!y.leaf) {
            System.arraycopy(y.C, t, z.C, 0, t);
        }
        y.n = t - 1;
        shiftChildrenRight(i + 1);
        C[i + 1] = z;
        shiftKeysRight(i);
        keys[i] = y.keys[t - 1];
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
        int childIndex = newRoot.keys[0] < k ? 1 : 0;
        newRoot.C[childIndex].insertNonFull(k);
        root = newRoot;
    }
}

class Main {
    public static void main(String[] args) {
        BTree tree = new BTree(3);
        int[] values = {10, 20, 5, 6, 12, 30, 7, 17};
        for (int value : values) {
            tree.insert(value);
        }

        System.out.print("Traversal of the constructed tree is ");
        tree.traverse();
        System.out.println();

        printSearchResult(tree, 6);
        printSearchResult(tree, 15);
    }

    private static void printSearchResult(BTree tree, int key) {
        if (tree.search(key) != null) {
            System.out.println(" | Present");
        } else {
            System.out.println(" | Not Present");
        }
    }
}
