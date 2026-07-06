class BTree {
    private Node root;
    private final NodeFactory nodeFactory;

    public BTree(int minDegree) {
        this(minDegree, SearchStrategy.LINEAR);
    }

    public BTree(int minDegree, SearchStrategy searchStrategy) {
        this.root = null;
        this.nodeFactory = new NodeFactory(minDegree, searchStrategy);
    }

    @FunctionalInterface
    interface KeyVisitor {
        void visit(int key);
    }

    @FunctionalInterface
    interface SearchStrategy {
        int findFirstGE(KeyManager storage, int key);

        SearchStrategy LINEAR = (storage, key) -> {
            int i = 0;
            while (i < storage.size() && storage.getKey(i) < key) {
                i++;
            }
            return i;
        };

        SearchStrategy BINARY = (storage, key) -> {
            int lo = 0, hi = storage.size();
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (storage.getKey(mid) < key) lo = mid + 1;
                else hi = mid;
            }
            return lo;
        };
    }

    interface KeyManager {
        int size();
        boolean isFull();
        int getKey(int i);
        Node getChild(int i);
        void setChild(int i, Node child);
        void insertKey(int pos, int key);
        void insertChild(int pos, Node child);
        void copyKeysFrom(KeyManager src, int srcStart, int destStart, int count);
        void copyChildrenFrom(KeyManager src, int srcStart, int destStart, int count);
        void setSize(int n);
        KeyManager createSibling();
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
        private final SearchStrategy searchStrategy;

        NodeFactory(int minDegree, SearchStrategy searchStrategy) {
            this.minDegree = minDegree;
            this.searchStrategy = searchStrategy;
        }

        Node createLeaf() {
            return new Node(minDegree, true, searchStrategy, new NodeStorage(minDegree));
        }

        Node createInternal() {
            return new Node(minDegree, false, searchStrategy, new NodeStorage(minDegree));
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

    private static class NodeStorage implements KeyManager {
        private final int[] keys;
        private final Node[] children;
        private int numKeys;
        private final int capacity;
        private final int minDegree;

        NodeStorage(int minDegree) {
            this.minDegree = minDegree;
            this.capacity = 2 * minDegree - 1;
            this.keys = new int[capacity];
            this.children = new Node[2 * minDegree];
            this.numKeys = 0;
        }

        public int size() {
            return numKeys;
        }

        public boolean isFull() {
            return numKeys == capacity;
        }

        public int getKey(int i) {
            return keys[i];
        }

        public Node getChild(int i) {
            return children[i];
        }

        public void setChild(int i, Node child) {
            children[i] = child;
        }

        public void insertKey(int pos, int key) {
            for (int j = numKeys - 1; j >= pos; j--) {
                keys[j + 1] = keys[j];
            }
            keys[pos] = key;
            numKeys++;
        }

        public void insertChild(int pos, Node child) {
            for (int j = numKeys; j >= pos; j--) {
                children[j + 1] = children[j];
            }
            children[pos] = child;
        }

        public void copyKeysFrom(KeyManager src, int srcStart, int destStart, int count) {
            for (int j = 0; j < count; j++) {
                keys[destStart + j] = src.getKey(srcStart + j);
            }
        }

        public void copyChildrenFrom(KeyManager src, int srcStart, int destStart, int count) {
            for (int j = 0; j < count; j++) {
                children[destStart + j] = src.getChild(srcStart + j);
            }
        }

        public void setSize(int n) {
            numKeys = n;
        }

        public KeyManager createSibling() {
            return new NodeStorage(minDegree);
        }
    }

    private static class Node {
        final KeyManager storage;
        final int minDegree;
        final boolean leaf;
        final SearchStrategy searchStrategy;

        Node(int minDegree, boolean leaf, SearchStrategy searchStrategy, KeyManager storage) {
            this.minDegree = minDegree;
            this.leaf = leaf;
            this.searchStrategy = searchStrategy;
            this.storage = storage;
        }

        void insertNonFull(int key) {
            int i = searchStrategy.findFirstGE(storage, key);
            if (leaf) {
                storage.insertKey(i, key);
            } else {
                if (storage.getChild(i).storage.isFull()) {
                    splitChild(i, storage.getChild(i));
                    if (storage.getKey(i) < key) i++;
                }
                storage.getChild(i).insertNonFull(key);
            }
        }

        void splitChild(int i, Node y) {
            Node z = new Node(minDegree, y.leaf, searchStrategy, y.storage.createSibling());
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
            int i = searchStrategy.findFirstGE(storage, key);
            if (i < storage.size() && storage.getKey(i) == key) {
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
