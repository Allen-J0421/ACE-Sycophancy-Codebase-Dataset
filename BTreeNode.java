class BTreeNode {
    int[] keys;
    int minDegree;
    BTreeNode[] children;
    int keyCount;
    boolean leaf;

    BTreeNode(int minDegree, boolean leaf) {
        this.minDegree = minDegree;
        this.leaf = leaf;
        this.keys = new int[2 * minDegree - 1];
        this.children = new BTreeNode[2 * minDegree];
        this.keyCount = 0;
    }

    void insertNonFull(int key) {
        int i = keyCount - 1;
        if (leaf) {
            while (i >= 0 && keys[i] > key) {
                keys[i + 1] = keys[i];
                i--;
            }
            keys[i + 1] = key;
            keyCount++;
        } else {
            while (i >= 0 && keys[i] > key) {
                i--;
            }
            int childIndex = i + 1;
            if (children[childIndex].keyCount == 2 * minDegree - 1) {
                splitChild(childIndex, children[childIndex]);
                if (keys[childIndex] < key) {
                    childIndex++;
                }
            }
            children[childIndex].insertNonFull(key);
        }
    }

    void splitChild(int childIndex, BTreeNode child) {
        BTreeNode newChild = new BTreeNode(minDegree, child.leaf);
        newChild.keyCount = minDegree - 1;
        for (int j = 0; j < minDegree - 1; j++) {
            newChild.keys[j] = child.keys[j + minDegree];
        }
        if (!child.leaf) {
            for (int j = 0; j < minDegree; j++) {
                newChild.children[j] = child.children[j + minDegree];
            }
        }
        child.keyCount = minDegree - 1;
        for (int j = keyCount; j > childIndex; j--) {
            children[j + 1] = children[j];
        }
        children[childIndex + 1] = newChild;
        for (int j = keyCount - 1; j >= childIndex; j--) {
            keys[j + 1] = keys[j];
        }
        keys[childIndex] = child.keys[minDegree - 1];
        keyCount++;
    }

    void traverse() {
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

    BTreeNode search(int key) {
        int i = 0;
        while (i < keyCount && key > keys[i]) {
            i++;
        }
        if (i < keyCount && key == keys[i]) {
            return this;
        }
        if (leaf) {
            return null;
        }
        return children[i].search(key);
    }
}
