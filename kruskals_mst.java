import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

class KruskalMST {
    public static void main(String[] args) {
        Graph graph = new Graph.Builder(4)
            .edge(0, 1, 10)
            .edge(1, 3, 15)
            .edge(2, 3, 4)
            .edge(2, 0, 6)
            .edge(0, 3, 5)
            .build();

        try {
            new Graph.Builder(4).edge(0, 5, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        try {
            new Graph.Builder(-1);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        MstSolver minKruskal = new KruskalSolver(RankedUnionFind::new, new AscendingWeight(), new LoggingObserver());
        MstSolver maxKruskal = new KruskalSolver(RankedUnionFind::new, new DescendingWeight(), new LoggingObserver());
        MstSolver heaviestEdge = g -> g.edges().stream()
            .sorted(Comparator.comparingInt(Edge::weight).reversed())
            .limit(1)
            .collect(Collectors.toList());

        runAndPrint("Min spanning tree", minKruskal, graph);
        runAndPrint("Max spanning tree", maxKruskal, graph);
        runAndPrint("Heaviest single edge (lambda)", heaviestEdge, graph);
    }

    private static void runAndPrint(String label, MstSolver solver, Graph graph) {
        System.out.println("\n=== " + label + " ===");
        List<Edge> result = solver.solve(graph);
        int cost = result.stream().mapToInt(Edge::weight).sum();
        System.out.println("Edges: " + result + ", cost: " + cost);
    }
}

record Graph(int vertices, List<Edge> edges) {
    Graph {
        edges = Collections.unmodifiableList(new ArrayList<>(edges));
    }

    static class Builder {
        private final int vertices;
        private final List<Edge> edges = new ArrayList<>();

        Builder(int vertices) {
            if (vertices <= 0) {
                throw new IllegalArgumentException(
                    "Vertex count must be positive, got: " + vertices);
            }
            this.vertices = vertices;
        }

        Builder edge(int from, int to, int weight) {
            if (from < 0 || from >= vertices) {
                throw new IllegalArgumentException(
                    "Vertex " + from + " out of range [0, " + (vertices - 1) + "]");
            }
            if (to < 0 || to >= vertices) {
                throw new IllegalArgumentException(
                    "Vertex " + to + " out of range [0, " + (vertices - 1) + "]");
            }
            edges.add(new Edge(from, to, weight));
            return this;
        }

        Graph build() {
            return new Graph(vertices, edges);
        }
    }
}

@FunctionalInterface
interface MstSolver {
    List<Edge> solve(Graph graph);
}

class KruskalSolver implements MstSolver {
    private final UnionFindFactory ufFactory;
    private final EdgeSortingStrategy sortingStrategy;
    private final SolverObserver observer;

    KruskalSolver(UnionFindFactory ufFactory, EdgeSortingStrategy sortingStrategy) {
        this(ufFactory, sortingStrategy, new NoOpObserver());
    }

    KruskalSolver(UnionFindFactory ufFactory, EdgeSortingStrategy sortingStrategy, SolverObserver observer) {
        this.ufFactory = ufFactory;
        this.sortingStrategy = sortingStrategy;
        this.observer = observer;
    }

    @Override
    public List<Edge> solve(Graph graph) {
        List<Edge> sorted = graph.edges().stream()
            .sorted(sortingStrategy.comparator())
            .collect(Collectors.toList());

        UnionFind uf = ufFactory.create(graph.vertices());
        List<Edge> mst = new ArrayList<>();

        for (Edge e : sorted) {
            observer.onEdgeConsidered(e);
            if (uf.find(e.from()) != uf.find(e.to())) {
                uf.union(e.from(), e.to());
                mst.add(e);
                observer.onEdgeAccepted(e, mst.size());
                if (mst.size() == graph.vertices() - 1) break;
            } else {
                observer.onEdgeRejected(e);
            }
        }

        observer.onSolveComplete(mst, mst.stream().mapToInt(Edge::weight).sum());
        return mst;
    }
}

interface SolverObserver {
    void onEdgeConsidered(Edge edge);
    void onEdgeAccepted(Edge edge, int mstSize);
    void onEdgeRejected(Edge edge);
    void onSolveComplete(List<Edge> mst, int totalCost);
}

class NoOpObserver implements SolverObserver {
    @Override public void onEdgeConsidered(Edge edge) {}
    @Override public void onEdgeAccepted(Edge edge, int mstSize) {}
    @Override public void onEdgeRejected(Edge edge) {}
    @Override public void onSolveComplete(List<Edge> mst, int totalCost) {}
}

class LoggingObserver implements SolverObserver {
    @Override
    public void onEdgeConsidered(Edge edge) {
        System.out.println("Considering " + edge);
    }

    @Override
    public void onEdgeAccepted(Edge edge, int mstSize) {
        System.out.println("  Accepted  " + edge + " (MST size: " + mstSize + ")");
    }

    @Override
    public void onEdgeRejected(Edge edge) {
        System.out.println("  Rejected  " + edge + " (cycle)");
    }

    @Override
    public void onSolveComplete(List<Edge> mst, int totalCost) {
        System.out.println("Complete — edges: " + mst + ", total cost: " + totalCost);
    }
}

interface EdgeSortingStrategy {
    Comparator<Edge> comparator();
}

class AscendingWeight implements EdgeSortingStrategy {
    @Override
    public Comparator<Edge> comparator() {
        return Comparator.comparingInt(Edge::weight);
    }
}

class DescendingWeight implements EdgeSortingStrategy {
    @Override
    public Comparator<Edge> comparator() {
        return Comparator.comparingInt(Edge::weight).reversed();
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

record Edge(int from, int to, int weight) {
    @Override
    public String toString() {
        return "(" + from + "-" + to + ", w=" + weight + ")";
    }
}
