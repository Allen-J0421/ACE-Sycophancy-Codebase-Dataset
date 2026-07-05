import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

interface GraphView {
    int vertexCount();
    List<Integer> neighbors(int v);
}

class Graph implements GraphView {
    private final int vertices;
    private final List<List<Integer>> adj;

    Graph(int vertices) {
        this.vertices = vertices;
        adj = new ArrayList<>();
        for (int i = 0; i < vertices; i++)
            adj.add(new ArrayList<>());
    }

    void addEdge(int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    @Override
    public int vertexCount() {
        return vertices;
    }

    @Override
    public List<Integer> neighbors(int v) {
        return adj.get(v);
    }
}

interface Frontier {
    void add(int node);
    int poll();
    boolean isEmpty();
}

class QueueFrontier implements Frontier {
    private final Deque<Integer> deque = new ArrayDeque<>();

    @Override public void add(int node) { deque.addLast(node); }
    @Override public int poll()         { return deque.pollFirst(); }
    @Override public boolean isEmpty()  { return deque.isEmpty(); }
}

class StackFrontier implements Frontier {
    private final Deque<Integer> deque = new ArrayDeque<>();

    @Override public void add(int node) { deque.addFirst(node); }
    @Override public int poll()         { return deque.pollFirst(); }
    @Override public boolean isEmpty()  { return deque.isEmpty(); }
}

class GraphTraversal {
    private final GraphView graph;
    private final boolean[] visited;
    private final Supplier<Frontier> frontierFactory;

    GraphTraversal(GraphView graph, Supplier<Frontier> frontierFactory) {
        this.graph = graph;
        this.visited = new boolean[graph.vertexCount()];
        this.frontierFactory = frontierFactory;
    }

    void traverse(Consumer<Integer> visitor) {
        for (int i = 0; i < graph.vertexCount(); i++) {
            if (!visited[i])
                expandFrom(i, visitor);
        }
    }

    private void expandFrom(int src, Consumer<Integer> visitor) {
        Frontier frontier = frontierFactory.get();
        visited[src] = true;
        frontier.add(src);
        while (!frontier.isEmpty()) {
            int curr = frontier.poll();
            visitor.accept(curr);
            for (int neighbor : graph.neighbors(curr)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    frontier.add(neighbor);
                }
            }
        }
    }

    static List<Integer> collect(GraphView graph) {
        List<Integer> result = new ArrayList<>();
        new GraphTraversal(graph, QueueFrontier::new).traverse(result::add);
        return result;
    }
}

class BreadthFirstSearch {
    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(0, 3);
        graph.addEdge(4, 5);

        new GraphTraversal(graph, QueueFrontier::new).traverse(node -> System.out.print(node + " "));
    }
}
