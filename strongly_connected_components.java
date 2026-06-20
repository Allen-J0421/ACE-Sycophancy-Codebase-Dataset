import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class StronglyConnectedComponents {

    List<List<Integer>> findComponents(int vertexCount, List<List<Integer>> adjacency) {
        boolean[] visited = new boolean[vertexCount];
        Deque<Integer> finishingOrder = buildFinishingOrder(adjacency, visited);
        List<List<Integer>> reversedGraph = buildReversedGraph(vertexCount, adjacency);

        visited = new boolean[vertexCount];
        List<List<Integer>> components = new ArrayList<>();

        while (!finishingOrder.isEmpty()) {
            int vertex = finishingOrder.pop();
            if (visited[vertex]) {
                continue;
            }

            List<Integer> component = new ArrayList<>();
            collectComponent(vertex, reversedGraph, visited, component);
            components.add(component);
        }

        return components;
    }

    private Deque<Integer> buildFinishingOrder(List<List<Integer>> adjacency, boolean[] visited) {
        Deque<Integer> finishingOrder = new ArrayDeque<>();

        for (int vertex = 0; vertex < adjacency.size(); vertex++) {
            if (!visited[vertex]) {
                depthFirstSearch(vertex, adjacency, visited, finishingOrder);
            }
        }

        return finishingOrder;
    }

    private void depthFirstSearch(
        int vertex,
        List<List<Integer>> adjacency,
        boolean[] visited,
        Deque<Integer> finishingOrder
    ) {
        visited[vertex] = true;

        for (int neighbor : adjacency.get(vertex)) {
            if (!visited[neighbor]) {
                depthFirstSearch(neighbor, adjacency, visited, finishingOrder);
            }
        }

        finishingOrder.push(vertex);
    }

    private void collectComponent(
        int vertex,
        List<List<Integer>> reversedGraph,
        boolean[] visited,
        List<Integer> component
    ) {
        visited[vertex] = true;
        component.add(vertex);

        for (int neighbor : reversedGraph.get(vertex)) {
            if (!visited[neighbor]) {
                collectComponent(neighbor, reversedGraph, visited, component);
            }
        }
    }

    private List<List<Integer>> buildReversedGraph(int vertexCount, List<List<Integer>> adjacency) {
        List<List<Integer>> reversedGraph = createEmptyGraph(vertexCount);

        for (int vertex = 0; vertex < adjacency.size(); vertex++) {
            for (int neighbor : adjacency.get(vertex)) {
                reversedGraph.get(neighbor).add(vertex);
            }
        }

        return reversedGraph;
    }

    static List<List<Integer>> buildGraph(int vertexCount, int[][] edges) {
        List<List<Integer>> adjacency = createEmptyGraph(vertexCount);

        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            adjacency.get(from).add(to);
        }

        return adjacency;
    }

    private static List<List<Integer>> createEmptyGraph(int vertexCount) {
        List<List<Integer>> graph = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            graph.add(new ArrayList<>());
        }
        return graph;
    }

    private static void printComponents(List<List<Integer>> components) {
        System.out.println("Strongly Connected Components:");
        for (List<Integer> component : components) {
            for (int vertex : component) {
                System.out.print(vertex + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        StronglyConnectedComponents solver = new StronglyConnectedComponents();

        int vertexCount = 5;
        int[][] edges = {
            {0, 2}, {0, 3}, {1, 0}, {2, 1}, {3, 4}
        };

        List<List<Integer>> adjacency = buildGraph(vertexCount, edges);
        List<List<Integer>> components = solver.findComponents(vertexCount, adjacency);

        printComponents(components);
    }
}
