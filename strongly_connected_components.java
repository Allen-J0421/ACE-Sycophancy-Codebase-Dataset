import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

class StronglyConnectedComponents {

    private static void dfsOrder(int node, List<List<Integer>> graph, boolean[] visited, Deque<Integer> order) {
        visited[node] = true;
        for (int neighbor : graph.get(node)) {
            if (!visited[neighbor]) {
                dfsOrder(neighbor, graph, visited, order);
            }
        }
        order.push(node);
    }

    private static void dfsComponent(
            int node,
            List<List<Integer>> reverseGraph,
            boolean[] visited,
            List<Integer> component) {
        visited[node] = true;
        component.add(node);

        for (int neighbor : reverseGraph.get(node)) {
            if (!visited[neighbor]) {
                dfsComponent(neighbor, reverseGraph, visited, component);
            }
        }
    }

    int[][] kosaraju(List<List<Integer>> graph) {
        int vertexCount = graph.size();
        boolean[] visited = new boolean[vertexCount];
        Deque<Integer> order = new ArrayDeque<>();

        for (int node = 0; node < vertexCount; node++) {
            if (!visited[node]) {
                dfsOrder(node, graph, visited, order);
            }
        }

        List<List<Integer>> reverseGraph = buildReverseGraph(graph);
        Arrays.fill(visited, false);

        List<int[]> components = new ArrayList<>();
        while (!order.isEmpty()) {
            int node = order.pop();
            if (!visited[node]) {
                List<Integer> component = new ArrayList<>();
                dfsComponent(node, reverseGraph, visited, component);
                components.add(toIntArray(component));
            }
        }

        return components.toArray(new int[components.size()][]);
    }

    private static List<List<Integer>> buildReverseGraph(List<List<Integer>> graph) {
        int vertexCount = graph.size();
        List<List<Integer>> reverseGraph = new ArrayList<>(vertexCount);
        for (int node = 0; node < vertexCount; node++) {
            reverseGraph.add(new ArrayList<>());
        }

        for (int source = 0; source < vertexCount; source++) {
            for (int target : graph.get(source)) {
                reverseGraph.get(target).add(source);
            }
        }

        return reverseGraph;
    }

    static List<List<Integer>> buildGraph(int vertexCount, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>(vertexCount);
        for (int node = 0; node < vertexCount; node++) {
            graph.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
        }

        return graph;
    }

    private static int[] toIntArray(List<Integer> values) {
        int[] result = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    public static void main(String[] args) {
        StronglyConnectedComponents solver = new StronglyConnectedComponents();
        int vertexCount = 6;
        int[][] edges = {
            {0, 1},
            {1, 2},
            {2, 0},
            {1, 3},
            {3, 4},
            {4, 5},
            {5, 3}
        };

        List<List<Integer>> graph = buildGraph(vertexCount, edges);
        int[][] components = solver.kosaraju(graph);

        System.out.println("Strongly Connected Components:");
        for (int[] component : components) {
            System.out.println(Arrays.toString(component));
        }
    }
}
