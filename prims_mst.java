import java.util.*;
import mst.WeightedEdge;

interface Graph<W extends Comparable<W>> {
    int vertexCount();
    W weight(int u, int v);
    Iterable<Integer> neighbors(int u);
}

class SparseGraph<W extends Comparable<W>> implements Graph<W> {
    private final List<List<WeightedEdge<W>>> adjList;

    SparseGraph(int vertexCount) {
        adjList = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    void addEdge(int u, int v, W weight) {
        adjList.get(u).add(new WeightedEdge<>(u, v, weight));
        adjList.get(v).add(new WeightedEdge<>(v, u, weight));
    }

    @Override
    public int vertexCount() {
        return adjList.size();
    }

    @Override
    public W weight(int u, int v) {
        for (WeightedEdge<W> edge : adjList.get(u)) {
            if (edge.to() == v) return edge.weight();
        }
        return null;
    }

    @Override
    public Iterable<Integer> neighbors(int u) {
        return () -> adjList.get(u).stream().map(WeightedEdge::to).iterator();
    }
}

class GraphFactory {

    static <W extends Comparable<W>> Builder<W> sparse(int vertexCount) {
        return new Builder<W>(new SparseGraph<>(vertexCount));
    }

    static class Builder<W extends Comparable<W>> {
        private final SparseGraph<W> graph;

        private Builder(SparseGraph<W> graph) {
            this.graph = graph;
        }

        Builder<W> edge(int u, int v, W weight) {
            graph.addEdge(u, v, weight);
            return this;
        }

        Graph<W> build() {
            return graph;
        }
    }
}

record Node<W extends Comparable<W>>(W key, int vertex) implements Comparable<Node<W>> {
    @Override
    public int compareTo(Node<W> other) {
        if (this.key == null) return other.key == null ? 0 : -1;
        if (other.key == null) return 1;
        return this.key.compareTo(other.key);
    }
}

interface MSTAlgorithm<W extends Comparable<W>> {
    List<WeightedEdge<W>> compute(Graph<W> graph);
}

class PrimsMSTAlgorithm<W extends Comparable<W>> implements MSTAlgorithm<W> {

    @Override
    public List<WeightedEdge<W>> compute(Graph<W> graph) {
        int V = graph.vertexCount();
        int[] parent = new int[V];
        Map<Integer, W> key = new HashMap<>();  // absent = infinity
        boolean[] inMST = new boolean[V];

        Arrays.fill(parent, -1);

        PriorityQueue<Node<W>> pq = new PriorityQueue<>();
        pq.offer(new Node<>(null, 0));  // null key = source sentinel, sorts first

        while (!pq.isEmpty()) {
            int u = pq.poll().vertex();
            if (inMST[u]) continue;  // stale entry
            inMST[u] = true;

            for (int v : graph.neighbors(u)) {
                W w = graph.weight(u, v);
                if (!inMST[v] && (!key.containsKey(v) || w.compareTo(key.get(v)) < 0)) {
                    parent[v] = u;
                    key.put(v, w);
                    pq.offer(new Node<>(w, v));
                }
            }
        }

        return buildEdgeList(parent, graph);
    }

    private List<WeightedEdge<W>> buildEdgeList(int[] parent, Graph<W> graph) {
        List<WeightedEdge<W>> edges = new ArrayList<>();
        for (int i = 1; i < parent.length; i++) {
            edges.add(new WeightedEdge<>(parent[i], i, graph.weight(parent[i], i)));
        }
        return edges;
    }
}

class MST {

    public static void main(String[] args) {
        Graph<Double> graph = GraphFactory.<Double>sparse(5)
            .edge(0, 1, 2.0)
            .edge(0, 3, 6.0)
            .edge(1, 2, 3.0)
            .edge(1, 3, 8.0)
            .edge(1, 4, 5.0)
            .edge(2, 4, 7.0)
            .edge(3, 4, 9.0)
            .build();

        MSTAlgorithm<Double> algorithm = new PrimsMSTAlgorithm<>();
        List<WeightedEdge<Double>> mst = algorithm.compute(graph);

        System.out.println("Edge \tWeight");
        for (WeightedEdge<Double> edge : mst) {
            System.out.println(edge);
        }
    }
}
