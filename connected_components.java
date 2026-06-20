import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

class ConnectedComponents {

    private ConnectedComponents() {
    }

    static void bfs(
            ArrayList<ArrayList<Integer>> adjacencyList,
            int sourceVertex,
            boolean[] visited,
            ArrayList<Integer> component) {
        validateVertex(adjacencyList, sourceVertex);

        Queue<Integer> pendingVertices = new ArrayDeque<>();
        visited[sourceVertex] = true;
        pendingVertices.add(sourceVertex);

        while (!pendingVertices.isEmpty()) {
            int currentVertex = pendingVertices.remove();
            component.add(currentVertex);

            for (int neighbor : adjacencyList.get(currentVertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    pendingVertices.add(neighbor);
                }
            }
        }
    }

    static ArrayList<ArrayList<Integer>> getComponents(ArrayList<ArrayList<Integer>> adjacencyList) {
        validateAdjacencyList(adjacencyList);

        int vertexCount = adjacencyList.size();
        boolean[] visited = new boolean[vertexCount];
        ArrayList<ArrayList<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            if (!visited[vertex]) {
                ArrayList<Integer> component = new ArrayList<>();
                bfs(adjacencyList, vertex, visited, component);
                components.add(component);
            }
        }

        return components;
    }

    static void addEdge(ArrayList<ArrayList<Integer>> adjacencyList, int fromVertex, int toVertex) {
        validateVertex(adjacencyList, fromVertex);
        validateVertex(adjacencyList, toVertex);

        adjacencyList.get(fromVertex).add(toVertex);
        adjacencyList.get(toVertex).add(fromVertex);
    }

    private static ArrayList<ArrayList<Integer>> createAdjacencyList(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }

        ArrayList<ArrayList<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }

        return adjacencyList;
    }

    private static void printComponents(List<? extends List<Integer>> components) {
        for (List<Integer> component : components) {
            for (int vertex : component) {
                System.out.print(vertex + " ");
            }
            System.out.println();
        }
    }

    private static void validateAdjacencyList(List<? extends List<Integer>> adjacencyList) {
        for (int vertex = 0; vertex < adjacencyList.size(); vertex++) {
            for (int neighbor : adjacencyList.get(vertex)) {
                if (neighbor < 0 || neighbor >= adjacencyList.size()) {
                    throw new IllegalArgumentException(
                            "Neighbor index out of bounds at vertex " + vertex + ": " + neighbor);
                }
            }
        }
    }

    private static void validateVertex(List<? extends List<Integer>> adjacencyList, int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("Vertex index out of bounds: " + vertex);
        }
    }

    public static void main(String[] args) {
        int vertexCount = 6;
        ArrayList<ArrayList<Integer>> adjacencyList = createAdjacencyList(vertexCount);

        addEdge(adjacencyList, 1, 2);
        addEdge(adjacencyList, 0, 3);
        addEdge(adjacencyList, 2, 0);
        addEdge(adjacencyList, 5, 4);

        ArrayList<ArrayList<Integer>> components = getComponents(adjacencyList);
        printComponents(components);
    }
}
