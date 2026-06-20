import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public final class StronglyConnectedComponents {

    public static void main(String[] args) {
        DirectedGraph graph = DirectedGraph.fromEdges(
            5,
            new int[][] {
                {0, 2}, {0, 3}, {1, 0}, {2, 1}, {3, 4}
            }
        );

        KosarajuStronglyConnectedComponents solver = new KosarajuStronglyConnectedComponents();
        List<List<Integer>> components = solver.findComponents(graph);

        System.out.println(formatComponents(components));
    }

    private static String formatComponents(List<List<Integer>> components) {
        StringBuilder output = new StringBuilder("Strongly Connected Components:\n");

        for (List<Integer> component : components) {
            for (int vertex : component) {
                output.append(vertex).append(' ');
            }
            output.append('\n');
        }

        return output.toString();
    }
}

final class DirectedGraph {
    private final List<List<Integer>> adjacency;

    private DirectedGraph(List<List<Integer>> adjacency) {
        this.adjacency = adjacency;
    }

    static DirectedGraph fromEdges(int vertexCount, int[][] edges) {
        List<List<Integer>> adjacency = createEmptyAdjacency(vertexCount);

        for (int[] edge : edges) {
            if (edge.length != 2) {
                throw new IllegalArgumentException("Each edge must contain exactly two vertices.");
            }

            int from = edge[0];
            int to = edge[1];
            validateVertex(from, vertexCount);
            validateVertex(to, vertexCount);
            adjacency.get(from).add(to);
        }

        return new DirectedGraph(freeze(adjacency));
    }

    int vertexCount() {
        return adjacency.size();
    }

    List<Integer> neighborsOf(int vertex) {
        validateVertex(vertex, vertexCount());
        return adjacency.get(vertex);
    }

    DirectedGraph reverse() {
        List<List<Integer>> reversedAdjacency = createEmptyAdjacency(vertexCount());

        for (int vertex = 0; vertex < vertexCount(); vertex++) {
            for (int neighbor : neighborsOf(vertex)) {
                reversedAdjacency.get(neighbor).add(vertex);
            }
        }

        return new DirectedGraph(freeze(reversedAdjacency));
    }

    private static List<List<Integer>> createEmptyAdjacency(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count must be non-negative.");
        }

        List<List<Integer>> adjacency = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacency.add(new ArrayList<>());
        }
        return adjacency;
    }

    private static List<List<Integer>> freeze(List<List<Integer>> adjacency) {
        List<List<Integer>> immutableAdjacency = new ArrayList<>(adjacency.size());

        for (List<Integer> neighbors : adjacency) {
            immutableAdjacency.add(List.copyOf(neighbors));
        }

        return Collections.unmodifiableList(immutableAdjacency);
    }

    private static void validateVertex(int vertex, int vertexCount) {
        if (vertex < 0 || vertex >= vertexCount) {
            throw new IllegalArgumentException(
                "Vertex " + vertex + " is out of bounds for graph size " + vertexCount + '.'
            );
        }
    }
}

final class KosarajuStronglyConnectedComponents {

    List<List<Integer>> findComponents(DirectedGraph graph) {
        boolean[] visited = new boolean[graph.vertexCount()];
        Deque<Integer> finishingOrder = new ArrayDeque<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                fillFinishingOrder(vertex, graph, visited, finishingOrder);
            }
        }

        DirectedGraph reversedGraph = graph.reverse();
        visited = new boolean[graph.vertexCount()];
        List<List<Integer>> components = new ArrayList<>();

        while (!finishingOrder.isEmpty()) {
            int vertex = finishingOrder.pop();
            if (visited[vertex]) {
                continue;
            }

            List<Integer> component = new ArrayList<>();
            collectComponent(vertex, reversedGraph, visited, component);
            components.add(List.copyOf(component));
        }

        return Collections.unmodifiableList(components);
    }

    private void fillFinishingOrder(
        int vertex,
        DirectedGraph graph,
        boolean[] visited,
        Deque<Integer> finishingOrder
    ) {
        visited[vertex] = true;

        for (int neighbor : graph.neighborsOf(vertex)) {
            if (!visited[neighbor]) {
                fillFinishingOrder(neighbor, graph, visited, finishingOrder);
            }
        }

        finishingOrder.push(vertex);
    }

    private void collectComponent(
        int vertex,
        DirectedGraph graph,
        boolean[] visited,
        List<Integer> component
    ) {
        visited[vertex] = true;
        component.add(vertex);

        for (int neighbor : graph.neighborsOf(vertex)) {
            if (!visited[neighbor]) {
                collectComponent(neighbor, graph, visited, component);
            }
        }
    }
}
