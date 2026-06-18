import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

final class Graph {
    private final List<List<Integer>> adjacencyList;

    Graph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }

        adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    int vertexCount() {
        return adjacencyList.size();
    }

    void addUndirectedEdge(int from, int to) {
        validateVertex(from);
        validateVertex(to);

        adjacencyList.get(from).add(to);
        adjacencyList.get(to).add(from);
    }

    List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("Vertex out of range: " + vertex);
        }
    }
}

final class BreadthFirstTraversal {
    private BreadthFirstTraversal() {
    }

    static List<Integer> traverse(Graph graph) {
        boolean[] visited = new boolean[graph.vertexCount()];
        List<Integer> traversalOrder = new ArrayList<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                traverseComponent(graph, vertex, visited, traversalOrder);
            }
        }

        return traversalOrder;
    }

    private static void traverseComponent(
        Graph graph,
        int startVertex,
        boolean[] visited,
        List<Integer> traversalOrder
    ) {
        Deque<Integer> queue = new ArrayDeque<>();
        visited[startVertex] = true;
        queue.add(startVertex);

        while (!queue.isEmpty()) {
            int currentVertex = queue.remove();
            traversalOrder.add(currentVertex);

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }
}

class BreadthFirstSearch {
    public static void main(String[] args) {
        Graph graph = buildSampleGraph();
        List<Integer> traversalOrder = BreadthFirstTraversal.traverse(graph);

        printTraversal(traversalOrder);
    }

    private static Graph buildSampleGraph() {
        Graph graph = new Graph(6);
        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(2, 0);
        graph.addUndirectedEdge(0, 3);
        graph.addUndirectedEdge(4, 5);
        return graph;
    }

    private static void printTraversal(List<Integer> traversalOrder) {
        for (int vertex : traversalOrder) {
            System.out.print(vertex + " ");
        }
    }
}
