class BTree {
    private Node root;
    private final NodeFactory nodeFactory;

    public BTree(int minDegree) {
        this.root = null;
        this.nodeFactory = new NodeFactory(minDegree);
    }

    @FunctionalInterface
    interface KeyVisitor {
        void visit(int key);
    }

    void traverse() {
        if (root != null) {
            root.traverse(key -> System.out.print(" " + key));
        }
    }

    boolean contains(int key) {
        return root != null && root.search(key) != null;
    }

    void insert(int key) {
        if (root == null) {
            root = nodeFactory.createRoot(key);
            return;
        }
        if (root.storage.isFull()) {
            root = nodeFactory.splitRoot(root);
        }
        root.insertNonFull(key);
    }

    private static class NodeFactory {
        private final int minDegree;

        NodeFactory(int minDegree) {
            this.minDegree = minDegree;
        }

        Node createLeaf() {
            return new Node(minDegree, true);
        }

        Node createInternal() {
            return new Node(minDegree, false);
        }

        Node createRoot(int key) {
            Node leaf = createLeaf();
            leaf.storage.insertKey(0, key);
            return leaf;
        }

        Node splitRoot(Node oldRoot) {
            Node newRoot = createInternal();
            newRoot.storage.setChild(0, oldRoot);
            newRoot.splitChild(0, oldRoot);
            return newRoot;
        }
    }

    private static class NodeStorage {
        private final int[] keys;
        private final Node[] children;
        private int numKeys;
        private final int capacity;

        NodeStorage(int minDegree) {
            this.capacity = 2 * minDegree - 1;
            this.keys = new int[capacity];
            this.children = new Node[2 * minDegree];
            this.numKeys = 0;
        }

        int size() {
            return numKeys;
        }

        boolean isFull() {
            return numKeys == capacity;
        }

        int getKey(int i) {
            return keys[i];
        }

        void setKey(int i, int key) {
            keys[i] = key;
        }

        Node getChild(int i) {
            return children[i];
        }

        void setChild(int i, Node child) {
            children[i] = child;
        }

        int findInsertionIndex(int key) {
            int i = numKeys - 1;
            while (i >= 0 && keys[i] > key) {
                i--;
            }
            return i;
        }

        void insertKey(int pos, int key) {
            for (int j = numKeys - 1; j >= pos; j--) {
                keys[j + 1] = keys[j];
            }
            keys[pos] = key;
            numKeys++;
        }

        void insertChild(int pos, Node child) {
            for (int j = numKeys; j >= pos; j--) {
                children[j + 1] = children[j];
            }
            children[pos] = child;
        }

        void copyKeysFrom(NodeStorage src, int srcStart, int destStart, int count) {
            for (int j = 0; j < count; j++) {
                keys[destStart + j] = src.keys[srcStart + j];
            }
        }

        void copyChildrenFrom(NodeStorage src, int srcStart, int destStart, int count) {
            for (int j = 0; j < count; j++) {
                children[destStart + j] = src.children[srcStart + j];
            }
        }

        void setSize(int n) {
            numKeys = n;
        }
    }

    private static class Node {
        final NodeStorage storage;
        final int minDegree;
        final boolean leaf;

        Node(int minDegree, boolean leaf) {
            this.minDegree = minDegree;
            this.leaf = leaf;
            this.storage = new NodeStorage(minDegree);
        }

        void insertNonFull(int key) {
            int i = storage.findInsertionIndex(key);
            if (leaf) {
                storage.insertKey(i + 1, key);
            } else {
                if (storage.getChild(i + 1).storage.isFull()) {
                    splitChild(i + 1, storage.getChild(i + 1));
                    if (storage.getKey(i + 1) < key) {
                        i++;
                    }
                }
                storage.getChild(i + 1).insertNonFull(key);
            }
        }

        void splitChild(int i, Node y) {
            Node z = new Node(minDegree, y.leaf);
            z.storage.copyKeysFrom(y.storage, minDegree, 0, minDegree - 1);
            if (!y.leaf) {
                z.storage.copyChildrenFrom(y.storage, minDegree, 0, minDegree);
            }
            z.storage.setSize(minDegree - 1);
            y.storage.setSize(minDegree - 1);
            storage.insertChild(i + 1, z);
            storage.insertKey(i, y.storage.getKey(minDegree - 1));
        }

        void traverse(KeyVisitor visitor) {
            for (int i = 0; i < storage.size(); i++) {
                if (!leaf) {
                    storage.getChild(i).traverse(visitor);
                }
                visitor.visit(storage.getKey(i));
            }
            if (!leaf) {
                storage.getChild(storage.size()).traverse(visitor);
            }
        }

        Node search(int key) {
            int i = 0;
            while (i < storage.size() && key > storage.getKey(i)) {
                i++;
            }
            if (i < storage.size() && key == storage.getKey(i)) {
                return this;
            }
            if (leaf) {
                return null;
            }
            return storage.getChild(i).search(key);
        }
    }
}

class Main {
    public static void main(String[] args) {
        BTree t = new BTree(3);
        t.insert(10);
        t.insert(20);
        t.insert(5);
        t.insert(6);
        t.insert(12);
        t.insert(30);
        t.insert(7);
        t.insert(17);

        System.out.print("Traversal of the constructed tree is ");
        t.traverse();
        System.out.println();

        int key = 6;
        if (t.contains(key)) {
            System.out.println(" | Present");
        } else {
            System.out.println(" | Not Present");
        }

        key = 15;
        if (t.contains(key)) {
            System.out.println(" | Present");
        } else {
            System.out.println(" | Not Present");
        }
    }
}
