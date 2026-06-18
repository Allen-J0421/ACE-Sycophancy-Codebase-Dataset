import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public final class DepthFirstSearch {

    private DepthFirstSearch() {
    }

    public static void main(String[] args) {
        Graph graph = Graph.withVertices(6);
        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(0, 3);
        graph.addUndirectedEdge(2, 0);
        graph.addUndirectedEdge(5, 4);

        System.out.println(formatTraversal(graph.depthFirstTraversal()));
    }

    private static String formatTraversal(List<Integer> traversal) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < traversal.size(); index++) {
            if (index > 0) {
                builder.append(' ');
            }
            builder.append(traversal.get(index));
        }
        return builder.toString();
    }

    static final class Graph {
        private final List<List<Integer>> adjacencyList;

        private Graph(List<List<Integer>> adjacencyList) {
            this.adjacencyList = adjacencyList;
        }

        static Graph withVertices(int vertexCount) {
            if (vertexCount < 0) {
                throw new IllegalArgumentException("vertexCount must be non-negative");
            }

            List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
            for (int vertex = 0; vertex < vertexCount; vertex++) {
                adjacencyList.add(new ArrayList<>());
            }

            return new Graph(adjacencyList);
        }

        void addUndirectedEdge(int from, int to) {
            validateVertex(from);
            validateVertex(to);

            adjacencyList.get(from).add(to);
            adjacencyList.get(to).add(from);
        }

        List<Integer> depthFirstTraversal() {
            boolean[] visited = new boolean[adjacencyList.size()];
            List<Integer> traversal = new ArrayList<>();

            for (int vertex = 0; vertex < adjacencyList.size(); vertex++) {
                if (!visited[vertex]) {
                    traverseComponent(vertex, visited, traversal);
                }
            }

            return traversal;
        }

        List<Integer> depthFirstTraversalFrom(int startVertex) {
            validateVertex(startVertex);

            boolean[] visited = new boolean[adjacencyList.size()];
            List<Integer> traversal = new ArrayList<>();
            traverseComponent(startVertex, visited, traversal);
            return traversal;
        }

        private void traverseComponent(int startVertex,
                                       boolean[] visited,
                                       List<Integer> traversal) {
            Deque<Integer> stack = new ArrayDeque<>();
            stack.push(startVertex);

            while (!stack.isEmpty()) {
                int vertex = stack.pop();
                if (visited[vertex]) {
                    continue;
                }

                visited[vertex] = true;
                traversal.add(vertex);

                List<Integer> neighbors = adjacencyList.get(vertex);
                for (int index = neighbors.size() - 1; index >= 0; index--) {
                    int neighbor = neighbors.get(index);
                    if (!visited[neighbor]) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        private void validateVertex(int vertex) {
            if (vertex < 0 || vertex >= adjacencyList.size()) {
                throw new IllegalArgumentException("vertex out of bounds: " + vertex);
            }
        }
    }
}
