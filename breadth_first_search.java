import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

class BreadthFirstSearch {

    private static final class UndirectedGraph {
        private final int vertexCount;
        private final List<List<Integer>> adjacencyList;

        UndirectedGraph(int vertexCount) {
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

        int vertexCount() {
            return vertexCount;
        }

        List<Integer> neighborsOf(int vertex) {
            validateVertex(vertex);
            return Collections.unmodifiableList(adjacencyList.get(vertex));
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= vertexCount) {
                throw new IllegalArgumentException("vertex out of range: " + vertex);
            }
        }
    }

    private static final class BreadthFirstTraversal {

        List<Integer> traverse(UndirectedGraph graph) {
            boolean[] visited = new boolean[graph.vertexCount()];
            List<Integer> traversal = new ArrayList<>(graph.vertexCount());

            for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
                if (!visited[vertex]) {
                    traverseComponent(graph, vertex, visited, traversal);
                }
            }

            return Collections.unmodifiableList(traversal);
        }

        private void traverseComponent(
                UndirectedGraph graph,
                int source,
                boolean[] visited,
                List<Integer> traversal
        ) {
            Queue<Integer> queue = new ArrayDeque<>();
            visited[source] = true;
            queue.add(source);

            while (!queue.isEmpty()) {
                int current = queue.poll();
                traversal.add(current);

                enqueueUnvisitedNeighbors(graph, current, visited, queue);
            }
        }

        private void enqueueUnvisitedNeighbors(
                UndirectedGraph graph,
                int current,
                boolean[] visited,
                Queue<Integer> queue
        ) {
            for (int neighbor : graph.neighborsOf(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }

    public static void main(String[] args) {
        UndirectedGraph graph = createSampleGraph();
        BreadthFirstTraversal traversal = new BreadthFirstTraversal();
        printTraversal(traversal.traverse(graph));
    }

    private static UndirectedGraph createSampleGraph() {
        UndirectedGraph graph = new UndirectedGraph(6);
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
