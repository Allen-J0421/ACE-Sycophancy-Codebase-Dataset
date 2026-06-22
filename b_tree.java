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

    static BTreeNode leafWithSingleKey(int minimumDegree, int key) {
        BTreeNode node = new BTreeNode(minimumDegree, true);
        node.initializeWithSingleKey(key);
        return node;
    }

    static BTreeNode internalRootWithFirstChild(int minimumDegree, BTreeNode firstChild) {
        BTreeNode node = new BTreeNode(minimumDegree, false);
        node.setChild(0, firstChild);
        return node;
    }

    private void initializeWithSingleKey(int key) {
        keys[0] = key;
        keyCount = 1;
    }

    private void setChild(int index, BTreeNode child) {
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
        return shouldInsertIntoRightChild(childIndex, key) ? childIndex + 1 : childIndex;
    }

    void splitChild(int childIndex, BTreeNode child) {
        BTreeNode rightSibling = createRightSibling(child);
        int medianKey = medianKey(child);
        trimToLeftHalf(child);
        insertChildAt(childIndex + 1, rightSibling);
        insertKeyAt(childIndex, medianKey);
    }

    private BTreeNode createRightSibling(BTreeNode node) {
        BTreeNode sibling = new BTreeNode(node.minimumDegree, node.leaf);
        sibling.keyCount = node.minimumDegree - 1;
        System.arraycopy(node.keys, node.minimumDegree, sibling.keys, 0, node.minimumDegree - 1);
        if (!node.leaf) {
            System.arraycopy(node.children, node.minimumDegree, sibling.children, 0, node.minimumDegree);
        }
        return sibling;
    }

    private int medianKey(BTreeNode node) {
        return node.keys[node.minimumDegree - 1];
    }

    private void trimToLeftHalf(BTreeNode node) {
        node.keyCount = node.minimumDegree - 1;
    }

    private void insertChildAt(int index, BTreeNode child) {
        shiftChildrenRight(index);
        children[index] = child;
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
        if (hasKeyAt(index, key)) {
            return this;
        }
        if (leaf) {
            return null;
        }
        return children[index].search(key);
    }

    private boolean hasKeyAt(int index, int key) {
        return index < keyCount && key == keys[index];
    }

    private int searchPosition(int key) {
        int index = 0;
        while (index < keyCount && key > keys[index]) {
            index++;
        }
        return index;
    }

    void insertAfterRootSplit(int key) {
        int childIndex = shouldInsertIntoRightChild(0, key) ? 1 : 0;
        children[childIndex].insertNonFull(key);
    }

    private boolean shouldInsertIntoRightChild(int childIndex, int key) {
        return keys[childIndex] < key;
    }
}

class BTree {
    private BTreeNode root;
    private final int minimumDegree;

    public BTree(int minimumDegree) {
        this.root = null;
        this.minimumDegree = minimumDegree;
    }

    String traversal() {
        StringBuilder traversal = new StringBuilder();
        if (!isEmpty()) {
            root.appendTraversal(traversal);
        }
        return traversal.toString();
    }

    void traverse() {
        System.out.print(traversal());
    }

    BTreeNode search(int key) {
        return isEmpty() ? null : root.search(key);
    }

    boolean contains(int key) {
        return search(key) != null;
    }

    void insertAll(int[] keys) {
        for (int key : keys) {
            insert(key);
        }
    }

    void insert(int key) {
        if (isEmpty()) {
            createRoot(key);
            return;
        }

        if (root.isFull()) {
            splitRootAndInsert(key);
            return;
        }

        root.insertNonFull(key);
    }

    private boolean isEmpty() {
        return root == null;
    }

    private void createRoot(int key) {
        root = BTreeNode.leafWithSingleKey(minimumDegree, key);
    }

    private void splitRootAndInsert(int key) {
        BTreeNode newRoot = splitRoot();
        newRoot.insertAfterRootSplit(key);
        root = newRoot;
    }

    private BTreeNode splitRoot() {
        BTreeNode newRoot = BTreeNode.internalRootWithFirstChild(minimumDegree, root);
        newRoot.splitChild(0, root);
        return newRoot;
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
        tree.insertAll(SAMPLE_VALUES);
        return tree;
    }

    private static void printSearchResult(BTree tree, int key) {
        System.out.println(searchMessage(tree, key));
    }

    private static String searchMessage(BTree tree, int key) {
        return tree.contains(key) ? " | Present" : " | Not Present";
    }
}
