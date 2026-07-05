import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

interface GraphInterface {
    List<Integer> getNeighbors(int vertex);
    int size();
}

class Graph implements GraphInterface {
    private final ArrayList<ArrayList<Integer>> adj;

    Graph(int vertices) {
        adj = new ArrayList<>();
        for (int i = 0; i < vertices; i++)
            adj.add(new ArrayList<>());
    }

    public void addEdge(int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    @Override
    public List<Integer> getNeighbors(int vertex) {
        return adj.get(vertex);
    }

    @Override
    public int size() {
        return adj.size();
    }
}

class TraversalResult {
    private final String algorithm;
    private final List<Integer> visitOrder;

    TraversalResult(String algorithm, List<Integer> visitOrder) {
        this.algorithm = algorithm;
        this.visitOrder = Collections.unmodifiableList(new ArrayList<>(visitOrder));
    }

    public String getAlgorithm() { return algorithm; }
    public List<Integer> getVisitOrder() { return visitOrder; }
    public int size() { return visitOrder.size(); }

    @Override
    public String toString() {
        return algorithm + ": " + visitOrder;
    }
}

interface TraversalStrategy {
    TraversalResult traverse(GraphInterface graph);
}

class DfsStrategy implements TraversalStrategy {
    private boolean[] visited;
    private final List<Integer> order = new ArrayList<>();

    @Override
    public TraversalResult traverse(GraphInterface graph) {
        visited = new boolean[graph.size()];
        order.clear();
        for (int i = 0; i < graph.size(); i++) {
            if (!visited[i]) visit(graph, i);
        }
        return new TraversalResult("DFS", order);
    }

    private void visit(GraphInterface graph, int vertex) {
        visited[vertex] = true;
        order.add(vertex);
        for (int neighbor : graph.getNeighbors(vertex)) {
            if (!visited[neighbor]) visit(graph, neighbor);
        }
    }
}

class BfsStrategy implements TraversalStrategy {
    @Override
    public TraversalResult traverse(GraphInterface graph) {
        boolean[] visited = new boolean[graph.size()];
        List<Integer> order = new ArrayList<>();
        Deque<Integer> queue = new ArrayDeque<>();

        for (int i = 0; i < graph.size(); i++) {
            if (!visited[i]) {
                visited[i] = true;
                queue.add(i);
                while (!queue.isEmpty()) {
                    int v = queue.poll();
                    order.add(v);
                    for (int neighbor : graph.getNeighbors(v)) {
                        if (!visited[neighbor]) {
                            visited[neighbor] = true;
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }
        return new TraversalResult("BFS", order);
    }
}

class GraphTraverser {
    private TraversalStrategy strategy;

    GraphTraverser(TraversalStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(TraversalStrategy strategy) {
        this.strategy = strategy;
    }

    public TraversalResult traverse(GraphInterface graph) {
        return strategy.traverse(graph);
    }
}

public class DepthFirstSearch {
    public static void main(String[] args) {
        Graph g = new Graph(6);
        g.addEdge(1, 2);
        g.addEdge(0, 3);
        g.addEdge(2, 0);
        g.addEdge(5, 4);

        GraphTraverser traverser = new GraphTraverser(new DfsStrategy());
        System.out.println(traverser.traverse(g));

        traverser.setStrategy(new BfsStrategy());
        System.out.println(traverser.traverse(g));
    }
}
