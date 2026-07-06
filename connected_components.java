import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

interface Graph<T> {
    Iterable<T> vertices();
    Iterable<T> getNeighbors(T v);
}

class AdjacencyListGraph<T> implements Graph<T> {
    private Map<T, ArrayList<T>> adj;

    AdjacencyListGraph() {
        adj = new LinkedHashMap<>();
    }

    void addEdge(T u, T v) {
        adj.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
        adj.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
    }

    @Override
    public Iterable<T> vertices() {
        return adj.keySet();
    }

    @Override
    public Iterable<T> getNeighbors(T v) {
        return adj.getOrDefault(v, new ArrayList<>());
    }
}

class GraphTraversal {

    static class BFSContext<T> {
        private Set<T> visited;
        private ArrayList<T> component;

        BFSContext() {
            visited = new HashSet<>();
            component = new ArrayList<>();
        }

        boolean isVisited(T v) {
            return visited.contains(v);
        }

        void markVisited(T v) {
            visited.add(v);
        }

        void addToComponent(T v) {
            component.add(v);
        }

        void resetComponent() {
            component = new ArrayList<>();
        }

        ArrayList<T> getComponent() {
            return component;
        }
    }

    static <T> void bfs(Graph<T> g, T src, BFSContext<T> ctx) {
        Queue<T> q = new LinkedList<>();
        ctx.markVisited(src);
        q.add(src);

        while (!q.isEmpty()) {
            T curr = q.poll();
            ctx.addToComponent(curr);

            for (T x : g.getNeighbors(curr)) {
                if (!ctx.isVisited(x)) {
                    ctx.markVisited(x);
                    q.add(x);
                }
            }
        }
    }
}

class ConnectedComponents {

    static <T> ArrayList<ArrayList<T>> getComponents(Graph<T> g) {
        GraphTraversal.BFSContext<T> ctx = new GraphTraversal.BFSContext<>();
        ArrayList<ArrayList<T>> res = new ArrayList<>();

        for (T vertex : g.vertices()) {
            if (!ctx.isVisited(vertex)) {
                ctx.resetComponent();
                GraphTraversal.bfs(g, vertex, ctx);
                res.add(ctx.getComponent());
            }
        }
        return res;
    }

    public static void main(String[] args) {
        AdjacencyListGraph<Integer> g = new AdjacencyListGraph<>();

        g.addEdge(1, 2);
        g.addEdge(0, 3);
        g.addEdge(2, 0);
        g.addEdge(5, 4);

        ArrayList<ArrayList<Integer>> res = getComponents(g);

        for (ArrayList<Integer> component : res) {
            for (int vertex : component) {
                System.out.print(vertex + " ");
            }
            System.out.println();
        }
    }
}
