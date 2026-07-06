class BTree {
    private Node root;
    private final int minDegree;

    public BTree(int minDegree) {
        this.root = null;
        this.minDegree = minDegree;
    }

    void traverse() {
        if (root != null) {
            root.traverse();
        }
    }

    boolean contains(int key) {
        return root != null && root.search(key) != null;
    }

    void insert(int key) {
        if (root == null) {
            root = new Node(minDegree, true);
            root.keys[0] = key;
            root.numKeys = 1;
        } else {
            if (root.numKeys == 2 * minDegree - 1) {
                Node s = new Node(minDegree, false);
                s.children[0] = root;
                s.splitChild(0, root);
                int i = 0;
                if (s.keys[0] < key) {
                    i++;
                }
                s.children[i].insertNonFull(key);
                root = s;
            } else {
                root.insertNonFull(key);
            }
        }
    }

    private static class Node {
        int[] keys;
        final int minDegree;
        Node[] children;
        int numKeys;
        boolean leaf;

        Node(int minDegree, boolean leaf) {
            this.keys = new int[2 * minDegree - 1];
            this.minDegree = minDegree;
            this.children = new Node[2 * minDegree];
            this.numKeys = 0;
            this.leaf = leaf;
        }

        void insertNonFull(int key) {
            int i = numKeys - 1;
            if (leaf) {
                while (i >= 0 && keys[i] > key) {
                    keys[i + 1] = keys[i];
                    i--;
                }
                keys[i + 1] = key;
                numKeys++;
            } else {
                while (i >= 0 && keys[i] > key) {
                    i--;
                }
                if (children[i + 1].numKeys == 2 * minDegree - 1) {
                    splitChild(i + 1, children[i + 1]);
                    if (keys[i + 1] < key) {
                        i++;
                    }
                }
                children[i + 1].insertNonFull(key);
            }
        }

        void splitChild(int i, Node y) {
            Node z = new Node(y.minDegree, y.leaf);
            z.numKeys = minDegree - 1;
            for (int j = 0; j < minDegree - 1; j++) {
                z.keys[j] = y.keys[j + minDegree];
            }
            if (!y.leaf) {
                for (int j = 0; j < minDegree; j++) {
                    z.children[j] = y.children[j + minDegree];
                }
            }
            y.numKeys = minDegree - 1;
            for (int j = numKeys; j > i; j--) {
                children[j + 1] = children[j];
            }
            children[i + 1] = z;
            for (int j = numKeys - 1; j >= i; j--) {
                keys[j + 1] = keys[j];
            }
            keys[i] = y.keys[minDegree - 1];
            numKeys++;
        }

        void traverse() {
            for (int i = 0; i < numKeys; i++) {
                if (!leaf) {
                    children[i].traverse();
                }
                System.out.print(" " + keys[i]);
            }
            if (!leaf) {
                children[numKeys].traverse();
            }
        }

        Node search(int key) {
            int i = 0;
            while (i < numKeys && key > keys[i]) {
                i++;
            }
            if (i < numKeys && key == keys[i]) {
                return this;
            }
            if (leaf) {
                return null;
            }
            return children[i].search(key);
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
