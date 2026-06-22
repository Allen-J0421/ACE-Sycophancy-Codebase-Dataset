class BTreeNode {
    private final int[] keys;
    private final int minimumDegree;
    private final BTreeNode[] children;
    private int keyCount;
    private final boolean leaf;

    public BTreeNode(int minimumDegree, boolean leaf) {
        this.keys = new int[2 * minimumDegree - 1];
        this.minimumDegree = minimumDegree;
        this.children = new BTreeNode[2 * minimumDegree];
        this.keyCount = 0;
        this.leaf = leaf;
    }

    void initializeWithSingleKey(int key) {
        keys[0] = key;
        keyCount = 1;
    }

    void setChild(int index, BTreeNode child) {
        children[index] = child;
    }

    boolean isFull() {
        return keyCount == maxKeys();
    }

    private int maxKeys() {
        return 2 * minimumDegree - 1;
    }

    private int insertionPosition(int key) {
        int index = keyCount - 1;
        while (index >= 0 && keys[index] > key) {
            index--;
        }
        return index + 1;
    }

    void insertNonFull(int key) {
        if (leaf) {
            insertIntoLeaf(key);
            return;
        }

        insertIntoChild(key);
    }

    private void insertIntoLeaf(int key) {
        insertKeyAt(insertionPosition(key), key);
    }

    private void insertIntoChild(int key) {
        int childIndex = insertionPosition(key);
        childIndex = prepareChildForInsert(childIndex, key);
        children[childIndex].insertNonFull(key);
    }

    private int prepareChildForInsert(int childIndex, int key) {
        if (!children[childIndex].isFull()) {
            return childIndex;
        }

        splitChild(childIndex, children[childIndex]);
        return keys[childIndex] < key ? childIndex + 1 : childIndex;
    }

    void splitChild(int childIndex, BTreeNode child) {
        BTreeNode rightSibling = createRightSibling(child);
        int medianKey = child.keys[minimumDegree - 1];
        child.keyCount = minimumDegree - 1;
        shiftChildrenRight(childIndex + 1);
        children[childIndex + 1] = rightSibling;
        insertKeyAt(childIndex, medianKey);
    }

    private BTreeNode createRightSibling(BTreeNode node) {
        BTreeNode sibling = new BTreeNode(node.minimumDegree, node.leaf);
        sibling.keyCount = minimumDegree - 1;
        System.arraycopy(node.keys, minimumDegree, sibling.keys, 0, minimumDegree - 1);
        if (!node.leaf) {
            System.arraycopy(node.children, minimumDegree, sibling.children, 0, minimumDegree);
        }
        return sibling;
    }

    private void insertKeyAt(int index, int key) {
        shiftKeysRight(index);
        keys[index] = key;
        keyCount++;
    }

    private void shiftKeysRight(int fromIndex) {
        System.arraycopy(keys, fromIndex, keys, fromIndex + 1, keyCount - fromIndex);
    }

    private void shiftChildrenRight(int fromIndex) {
        System.arraycopy(children, fromIndex, children, fromIndex + 1, keyCount - fromIndex + 1);
    }

    void traverse() {
        StringBuilder traversal = new StringBuilder();
        appendTraversal(traversal);
        System.out.print(traversal);
    }

    void appendTraversal(StringBuilder traversal) {
        for (int index = 0; index < keyCount; index++) {
            if (!leaf) {
                children[index].appendTraversal(traversal);
            }
            traversal.append(' ').append(keys[index]);
        }
        if (!leaf) {
            children[keyCount].appendTraversal(traversal);
        }
    }

    BTreeNode search(int key) {
        int index = searchPosition(key);
        if (index < keyCount && key == keys[index]) {
            return this;
        }
        if (leaf) {
            return null;
        }
        return children[index].search(key);
    }

    private int searchPosition(int key) {
        int index = 0;
        while (index < keyCount && key > keys[index]) {
            index++;
        }
        return index;
    }

    void insertAfterRootSplit(int key) {
        int childIndex = keys[0] < key ? 1 : 0;
        children[childIndex].insertNonFull(key);
    }
}

class BTree {
    BTreeNode root;
    int minimumDegree;

    public BTree(int minimumDegree) {
        this.root = null;
        this.minimumDegree = minimumDegree;
    }

    String traversal() {
        StringBuilder traversal = new StringBuilder();
        if (root != null) {
            root.appendTraversal(traversal);
        }
        return traversal.toString();
    }

    void traverse() {
        System.out.print(traversal());
    }

    BTreeNode search(int key) {
        return root == null ? null : root.search(key);
    }

    boolean contains(int key) {
        return search(key) != null;
    }

    void insert(int key) {
        if (root == null) {
            createRoot(key);
            return;
        }

        if (root.isFull()) {
            splitRootAndInsert(key);
            return;
        }

        root.insertNonFull(key);
    }

    private void createRoot(int key) {
        root = new BTreeNode(minimumDegree, true);
        root.initializeWithSingleKey(key);
    }

    private void splitRootAndInsert(int key) {
        BTreeNode newRoot = new BTreeNode(minimumDegree, false);
        newRoot.setChild(0, root);
        newRoot.splitChild(0, root);
        newRoot.insertAfterRootSplit(key);
        root = newRoot;
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
