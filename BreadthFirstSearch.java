import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class BreadthFirstSearch {

    static List<Integer> traverse(Graph graph) {
        int n = graph.vertexCount();
        boolean[] visited = new boolean[n];
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                traverseComponent(graph, i, visited, result);
            }
        }
        return result;
    }

    private static void traverseComponent(Graph graph, int source, boolean[] visited, List<Integer> result) {
        Queue<Integer> queue = new LinkedList<>();
        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            result.add(current);

            for (int neighbor : graph.neighbors(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }

    public static void main(String[] args) {
        Graph graph = new Graph(6);
        graph.addEdge(1, 2);
        graph.addEdge(2, 0);
        graph.addEdge(0, 3);
        graph.addEdge(4, 5);

        List<Integer> result = traverse(graph);
        for (int vertex : result) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
