import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class KruskalMST {
    public static void main(String[] args) {
        Graph graph = new Graph.Builder()
            .vertices(4)
            .edge(0, 1, 10)
            .edge(1, 3, 15)
            .edge(2, 3, 4)
            .edge(2, 0, 6)
            .edge(0, 3, 5)
            .build();

        KruskalSolver solver = new KruskalSolver(RankedUnionFind::new);
        List<Edge> mst = solver.solve(graph);
        int totalCost = mst.stream().mapToInt(e -> e.weight).sum();
        System.out.println("MST edges: " + mst);
        System.out.println("Total cost: " + totalCost);
    }
}

class Graph {
    final int vertices;
    final List<Edge> edges;

    private Graph(Builder b) {
        this.vertices = b.vertices;
        this.edges = Collections.unmodifiableList(b.edges);
    }

    static class Builder {
        private int vertices;
        private final List<Edge> edges = new ArrayList<>();

        Builder vertices(int n) {
            this.vertices = n;
            return this;
        }

        Builder edge(int from, int to, int weight) {
            edges.add(new Edge(from, to, weight));
            return this;
        }

        Graph build() {
            return new Graph(this);
        }
    }
}

class KruskalSolver {
    private final UnionFindFactory ufFactory;

    KruskalSolver(UnionFindFactory ufFactory) {
        this.ufFactory = ufFactory;
    }

    List<Edge> solve(Graph graph) {
        Edge[] sorted = graph.edges.toArray(new Edge[0]);
        Arrays.sort(sorted);

        UnionFind uf = ufFactory.create(graph.vertices);
        List<Edge> mst = new ArrayList<>();

        for (Edge e : sorted) {
            if (uf.find(e.from) != uf.find(e.to)) {
                uf.union(e.from, e.to);
                mst.add(e);
                if (mst.size() == graph.vertices - 1) break;
            }
        }
        return mst;
    }
}

interface UnionFindFactory {
    UnionFind create(int n);
}

interface UnionFind {
    int find(int i);
    void union(int x, int y);
}

class RankedUnionFind implements UnionFind {
    private final int[] parent, rank;

    RankedUnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 1;
        }
    }

    @Override
    public int find(int i) {
        if (parent[i] != i) {
            parent[i] = find(parent[i]);
        }
        return parent[i];
    }

    @Override
    public void union(int x, int y) {
        int rx = find(x), ry = find(y);
        if (rx == ry) return;
        if (rank[rx] < rank[ry]) {
            parent[rx] = ry;
        } else if (rank[rx] > rank[ry]) {
            parent[ry] = rx;
        } else {
            parent[ry] = rx;
            rank[rx]++;
        }
    }
}

class Edge implements Comparable<Edge> {
    final int from, to, weight;

    Edge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    @Override
    public String toString() {
        return "(" + from + "-" + to + ", w=" + weight + ")";
    }
}
