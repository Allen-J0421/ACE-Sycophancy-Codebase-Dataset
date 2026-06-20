import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

class ConnectedComponents {

    static List<List<Integer>> getComponents(List<? extends List<Integer>> adjacencyList) {
        return getComponents(UndirectedGraph.fromAdjacencyList(adjacencyList));
    }

    static List<List<Integer>> getComponents(UndirectedGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("graph cannot be null");
        }

        boolean[] visited = new boolean[graph.vertexCount()];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                List<Integer> component = new ArrayList<>();
                collectComponent(graph, vertex, visited, component);
                components.add(component);
            }
        }

        return components;
    }

    static void addEdge(List<? extends List<Integer>> adjacencyList, int firstVertex, int secondVertex) {
        validateAdjacencyList(adjacencyList);
        validateVertex(adjacencyList, firstVertex);
        validateVertex(adjacencyList, secondVertex);

        adjacencyList.get(firstVertex).add(secondVertex);
        adjacencyList.get(secondVertex).add(firstVertex);
    }

    private static void collectComponent(
            UndirectedGraph graph,
            int source,
            boolean[] visited,
            List<Integer> component
    ) {
        Queue<Integer> queue = new ArrayDeque<>();
        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            component.add(current);

            for (int neighbor : graph.neighbors(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }

    private static void validateAdjacencyList(List<? extends List<Integer>> adjacencyList) {
        if (adjacencyList == null) {
            throw new IllegalArgumentException("adjacencyList cannot be null");
        }

        for (int vertex = 0; vertex < adjacencyList.size(); vertex++) {
            List<Integer> neighbors = adjacencyList.get(vertex);
            if (neighbors == null) {
                throw new IllegalArgumentException("adjacency list cannot be null for vertex " + vertex);
            }
        }
    }

    private static void validateVertex(List<? extends List<Integer>> adjacencyList, int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("vertex out of range: " + vertex);
        }
    }

    private static void printComponents(List<List<Integer>> components) {
        for (List<Integer> component : components) {
            StringBuilder line = new StringBuilder();
            for (int vertex : component) {
                if (line.length() > 0) {
                    line.append(' ');
                }
                line.append(vertex);
            }
            System.out.println(line);
        }
    }

    public static void main(String[] args) {
        UndirectedGraph graph = UndirectedGraph.withVertexCount(6);

        graph.addEdge(1, 2);
        graph.addEdge(0, 3);
        graph.addEdge(2, 0);
        graph.addEdge(5, 4);

        printComponents(getComponents(graph));
    }
}

final class UndirectedGraph {
    private final List<List<Integer>> adjacencyList;

    private UndirectedGraph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    static UndirectedGraph withVertexCount(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }

        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
        return new UndirectedGraph(adjacencyList);
    }

    static UndirectedGraph fromAdjacencyList(List<? extends List<Integer>> source) {
        if (source == null) {
            throw new IllegalArgumentException("adjacencyList cannot be null");
        }

        List<List<Integer>> adjacencyList = new ArrayList<>(source.size());
        for (int vertex = 0; vertex < source.size(); vertex++) {
            List<Integer> neighbors = source.get(vertex);
            if (neighbors == null) {
                throw new IllegalArgumentException("adjacency list cannot be null for vertex " + vertex);
            }

            List<Integer> copiedNeighbors = new ArrayList<>(neighbors.size());
            for (Integer neighbor : neighbors) {
                validateVertex(source.size(), neighbor);
                copiedNeighbors.add(neighbor);
            }
            adjacencyList.add(copiedNeighbors);
        }

        return new UndirectedGraph(adjacencyList);
    }

    int vertexCount() {
        return adjacencyList.size();
    }

    List<Integer> neighbors(int vertex) {
        validateVertex(vertexCount(), vertex);
        return Collections.unmodifiableList(adjacencyList.get(vertex));
    }

    void addEdge(int firstVertex, int secondVertex) {
        validateVertex(vertexCount(), firstVertex);
        validateVertex(vertexCount(), secondVertex);

        adjacencyList.get(firstVertex).add(secondVertex);
        adjacencyList.get(secondVertex).add(firstVertex);
    }

    private static void validateVertex(int vertexCount, Integer vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("vertex cannot be null");
        }
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException("vertex out of range: " + vertex);
        }
    }
}
