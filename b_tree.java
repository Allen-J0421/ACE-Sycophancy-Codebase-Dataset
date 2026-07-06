import java.util.ArrayList;
import java.util.List;

class BTree {
    private Node root;
    private final NodeFactory nodeFactory;

    public BTree(int minDegree) {
        this(minDegree, SearchStrategies.LINEAR, InsertionStrategies.STANDARD);
    }

    public BTree(int minDegree, SearchStrategy searchStrategy) {
        this(minDegree, searchStrategy, InsertionStrategies.STANDARD);
    }

    public BTree(int minDegree, SearchStrategy searchStrategy, InsertionStrategy insertionStrategy) {
        this.root = null;
        this.nodeFactory = new NodeFactory(minDegree, searchStrategy, insertionStrategy);
    }

    @FunctionalInterface
    interface KeyVisitor {
        void visit(int key);
    }

    @FunctionalInterface
    interface SearchStrategy {
        int findFirstGE(KeyManager storage, int key);
    }

    interface InsertionStrategy {
        void split(Node parent, int childIndex, Node fullChild);
    }

    enum InsertionStrategies implements InsertionStrategy {
        STANDARD {
            public void split(Node parent, int childIndex, Node fullChild) {
                int minDegree = fullChild.minDegree;
                int medianKey = fullChild.storage.getKey(minDegree - 1);
                Node sibling = new Node(minDegree, fullChild.leaf,
                        fullChild.searchStrategy, fullChild.insertionStrategy,
                        fullChild.storage.createSibling());
                sibling.storage.copyKeysFrom(fullChild.storage, minDegree, 0, minDegree - 1);
                if (!fullChild.leaf) {
                    sibling.storage.copyChildrenFrom(fullChild.storage, minDegree, 0, minDegree);
                }
                sibling.storage.setSize(minDegree - 1);
                fullChild.storage.setSize(minDegree - 1);
                parent.storage.insertChild(childIndex + 1, sibling);
                parent.storage.insertKey(childIndex, medianKey);
            }
        }
    }

    interface KeyManager {
        int size();
        boolean isFull();
        int getKey(int i);
        Node getChild(int i);
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
        private final InsertionStrategy insertionStrategy;

        NodeFactory(int minDegree, SearchStrategy searchStrategy, InsertionStrategy insertionStrategy) {
            this.minDegree = minDegree;
            this.searchStrategy = searchStrategy;
            this.insertionStrategy = insertionStrategy;
        }

        Node createLeaf() {
            return new Node(minDegree, true, searchStrategy, insertionStrategy, new NodeStorage(minDegree));
        }

        Node createInternal() {
            return new Node(minDegree, false, searchStrategy, insertionStrategy, new NodeStorage(minDegree));
        }

        Node createRoot(int key) {
            Node leaf = createLeaf();
            leaf.storage.insertKey(0, key);
            return leaf;
        }

        Node splitRoot(Node oldRoot) {
            Node newRoot = createInternal();
            newRoot.storage.insertChild(0, oldRoot);
            insertionStrategy.split(newRoot, 0, oldRoot);
            return newRoot;
        }
    }

    private static class NodeStorage implements KeyManager {
        private final List<Integer> keys;
        private final List<Node> children;
        private final int capacity;
        private final int minDegree;

        NodeStorage(int minDegree) {
            this.minDegree = minDegree;
            this.capacity = 2 * minDegree - 1;
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
        }

        public int size() {
            return keys.size();
        }

        public boolean isFull() {
            return keys.size() == capacity;
        }

        public int getKey(int i) {
            return keys.get(i);
        }

        public Node getChild(int i) {
            return children.get(i);
        }

        public void insertKey(int pos, int key) {
            keys.add(pos, key);
        }

        public void insertChild(int pos, Node child) {
            children.add(pos, child);
        }

        public void copyKeysFrom(KeyManager src, int srcStart, int destStart, int count) {
            for (int j = 0; j < count; j++) {
                keys.add(destStart + j, src.getKey(srcStart + j));
            }
        }

        public void copyChildrenFrom(KeyManager src, int srcStart, int destStart, int count) {
            for (int j = 0; j < count; j++) {
                children.add(destStart + j, src.getChild(srcStart + j));
            }
        }

        public void setSize(int n) {
            keys.subList(n, keys.size()).clear();
            if (children.size() > n + 1) {
                children.subList(n + 1, children.size()).clear();
            }
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
        final InsertionStrategy insertionStrategy;

        Node(int minDegree, boolean leaf, SearchStrategy searchStrategy,
                InsertionStrategy insertionStrategy, KeyManager storage) {
            this.minDegree = minDegree;
            this.leaf = leaf;
            this.searchStrategy = searchStrategy;
            this.insertionStrategy = insertionStrategy;
            this.storage = storage;
        }

        void insertNonFull(int key) {
            int i = searchStrategy.findFirstGE(storage, key);
            if (leaf) {
                storage.insertKey(i, key);
            } else {
                if (storage.getChild(i).storage.isFull()) {
                    insertionStrategy.split(this, i, storage.getChild(i));
                    if (storage.getKey(i) < key) i++;
                }
                storage.getChild(i).insertNonFull(key);
            }
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

enum SearchStrategies implements BTree.SearchStrategy {
    LINEAR {
        public int findFirstGE(BTree.KeyManager storage, int key) {
            int i = 0;
            while (i < storage.size() && storage.getKey(i) < key) {
                i++;
            }
            return i;
        }
    },
    BINARY {
        public int findFirstGE(BTree.KeyManager storage, int key) {
            int lo = 0, hi = storage.size();
            while (lo < hi) {
                int mid = (lo + hi) >>> 1;
                if (storage.getKey(mid) < key) lo = mid + 1;
                else hi = mid;
            }
            return lo;
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
