interface DisjointSet {
    int find(int i);
    void union(int i, int j);
    boolean connected(int i, int j);
    int size();
    int count();
    void reset();
}

class Node {
    Node parent;
    int rank;
    final int index;

    Node(int index) {
        this.parent = this;
        this.rank = 0;
        this.index = index;
    }
}

class DisjointSetFactory {
    static DisjointSet create(int size) {
        return new UnionFind(size);
    }
}

public class UnionFind implements DisjointSet {
    private final Node[] nodes;
    private int count;

    public UnionFind(int size) {
        nodes = new Node[size];
        count = size;
        for (int i = 0; i < size; i++) {
            nodes[i] = new Node(i);
        }
    }

    private static void validate(int i, int size) {
        if (i < 0 || i >= size) {
            throw new IllegalArgumentException("Index " + i + " out of bounds for size " + size);
        }
    }

    public int find(int i) {
        validate(i, nodes.length);

        Node root = nodes[i];
        while (root.parent != root) {
            root = root.parent;
        }

        Node current = nodes[i];
        while (current != root) {
            Node next = current.parent;
            current.parent = root;
            current = next;
        }

        return root.index;
    }

    public void union(int i, int j) {
        validate(i, nodes.length);
        validate(j, nodes.length);

        int irep = find(i);
        int jrep = find(j);

        if (irep == jrep) return;

        count--;

        Node iRoot = nodes[irep];
        Node jRoot = nodes[jrep];

        if (iRoot.rank < jRoot.rank) {
            iRoot.parent = jRoot;
        } else if (iRoot.rank > jRoot.rank) {
            jRoot.parent = iRoot;
        } else {
            jRoot.parent = iRoot;
            iRoot.rank++;
        }
    }

    public int size() {
        return nodes.length;
    }

    public int count() {
        return count;
    }

    public boolean connected(int i, int j) {
        validate(i, nodes.length);
        validate(j, nodes.length);
        return find(i) == find(j);
    }

    public void reset() {
        count = nodes.length;
        for (Node node : nodes) {
            node.parent = node;
            node.rank = 0;
        }
    }

    public static void main(String[] args) {
        int size = 5;
        DisjointSet uf = DisjointSetFactory.create(size);
        uf.union(1, 2);
        uf.union(3, 4);
        System.out.println("Are 1 and 2 in the same set? " + uf.connected(1, 2));
    }
}
