import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

class BreadthFirstSearch {

    private static final class Graph {
        private final int vertexCount;
        private final List<List<Integer>> adjacencyList;

        Graph(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must not be negative");
            }

            this.vertexCount = vertexCount;
            adjacencyList = new ArrayList<>(vertexCount);
            for (int i = 0; i < vertexCount; i++) {
                adjacencyList.add(new ArrayList<>());
            }
        }

        void addEdge(int source, int destination) {
            validateVertex(source);
            validateVertex(destination);

            adjacencyList.get(source).add(destination);
            adjacencyList.get(destination).add(source);
        }

        List<Integer> breadthFirstTraversal() {
            boolean[] visited = new boolean[vertexCount];
            List<Integer> traversal = new ArrayList<>(vertexCount);

            for (int vertex = 0; vertex < vertexCount; vertex++) {
                if (!visited[vertex]) {
                    traverseComponent(vertex, visited, traversal);
                }
            }

            return Collections.unmodifiableList(traversal);
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= vertexCount) {
                throw new IllegalArgumentException("vertex out of range: " + vertex);
            }
        }

        private void traverseComponent(int source, boolean[] visited, List<Integer> traversal) {
            Queue<Integer> queue = new ArrayDeque<>();
            visited[source] = true;
            queue.add(source);

            while (!queue.isEmpty()) {
                int current = queue.poll();
                traversal.add(current);

                for (int neighbor : adjacencyList.get(current)) {
                    if (!visited[neighbor]) {
                        visited[neighbor] = true;
                        queue.add(neighbor);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Graph graph = createSampleGraph();
        printTraversal(graph.breadthFirstTraversal());
    }

    private static Graph createSampleGraph() {
        Graph graph = new Graph(6);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(0, 3);
        graph.addEdge(4, 5);
        return graph;
    }

    private static void printTraversal(List<Integer> traversal) {
        for (int vertex : traversal) {
            System.out.print(vertex + " ");
        }
    }
}
