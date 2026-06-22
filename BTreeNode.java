import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.IntConsumer;

final class BTreeNode {
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

    void attachChild(int index, BTreeNode child) {
        children[index] = child;
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
            splitChild(childIndex);
            if (keys[childIndex] < key) {
                childIndex++;
            }
        }
        children[childIndex].insertNonFull(key);
    }

    void splitChild(int childIndex) {
        BTreeNode fullChild = children[childIndex];
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
        StringJoiner joiner = new StringJoiner(" ");
        forEachKeyInOrder(key -> joiner.add(Integer.toString(key)));
        return joiner.toString();
    }

    @Override
    public String toString() {
        return traversalString();
    }

    List<Integer> keysInOrder() {
        List<Integer> output = new ArrayList<>();
        forEachKeyInOrder(output::add);
        return output;
    }

    void forEachKeyInOrder(IntConsumer consumer) {
        for (int i = 0; i < keyCount; i++) {
            if (!leaf) {
                children[i].forEachKeyInOrder(consumer);
            }
            consumer.accept(keys[i]);
        }
        if (!leaf) {
            children[keyCount].forEachKeyInOrder(consumer);
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
