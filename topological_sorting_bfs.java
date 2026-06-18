import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

class DirectedAcyclicGraph {
    private final int vertices;
    private final List<List<Integer>> adjacencyList;

    public DirectedAcyclicGraph(int vertices) {
        if (vertices <= 0) {
            throw new IllegalArgumentException("Number of vertices must be positive");
        }
        this.vertices = vertices;
        this.adjacencyList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            this.adjacencyList.add(new ArrayList<>());
        }
    }

    public void addEdge(int source, int destination) {
        validateVertex(source);
        validateVertex(destination);
        if (source == destination) {
            throw new IllegalArgumentException("Self-loops are not allowed in a DAG");
        }
        this.adjacencyList.get(source).add(destination);
    }

    public List<Integer> topologicalSort() {
        int[] indegree = calculateIndegrees();
        Queue<Integer> queue = initializeQueue(indegree);

        List<Integer> result = new ArrayList<>();
        processNodes(queue, indegree, result);

        if (result.size() != vertices) {
            throw new IllegalStateException("Graph contains a cycle");
        }

        return result;
    }

    private int[] calculateIndegrees() {
        int[] indegree = new int[vertices];
        for (int i = 0; i < vertices; i++) {
            for (int neighbor : adjacencyList.get(i)) {
                indegree[neighbor]++;
            }
        }
        return indegree;
    }

    private Queue<Integer> initializeQueue(int[] indegree) {
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < vertices; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
            }
        }
        return queue;
    }

    private void processNodes(Queue<Integer> queue, int[] indegree, List<Integer> result) {
        while (!queue.isEmpty()) {
            int node = queue.poll();
            result.add(node);

            for (int neighbor : adjacencyList.get(node)) {
                indegree[neighbor]--;
                if (indegree[neighbor] == 0) {
                    queue.add(neighbor);
                }
            }
        }
    }

    private void validateVertex(int vertex) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException(
                String.format("Vertex %d is out of bounds [0, %d]", vertex, vertices - 1)
            );
        }
    }
}

class TopologicalSortDemo {
    public static void main(String[] args) {
        DirectedAcyclicGraph graph = new DirectedAcyclicGraph(6);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(4, 5);
        graph.addEdge(5, 1);
        graph.addEdge(5, 2);

        List<Integer> result = graph.topologicalSort();
        System.out.println("Topological Sort: " + result);
    }
}
