import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

final class ConnectedComponents {

    private ConnectedComponents() {
    }

    static List<List<Integer>> findComponents(Graph graph) {
        boolean[] visited = new boolean[graph.size()];
        List<List<Integer>> components = new ArrayList<>();

        for (int vertex = 0; vertex < graph.size(); vertex++) {
            if (!visited[vertex]) {
                List<Integer> component = new ArrayList<>();
                bfs(graph, vertex, visited, component);
                components.add(component);
            }
        }
        return components;
    }

    private static void bfs(Graph graph, int source, boolean[] visited, List<Integer> component) {
        Queue<Integer> queue = new ArrayDeque<>();
        visited[source] = true;
        queue.add(source);

        while (!queue.isEmpty()) {
            int currentVertex = queue.remove();
            component.add(currentVertex);

            for (int neighbor : graph.neighborsOf(currentVertex)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
    }

    private static void printComponents(List<List<Integer>> components) {
        for (List<Integer> component : components) {
            System.out.println(joinIntegers(component));
        }
    }

    private static String joinIntegers(List<Integer> values) {
        StringBuilder line = new StringBuilder();
        for (int index = 0; index < values.size(); index++) {
            if (index > 0) {
                line.append(' ');
            }
            line.append(values.get(index));
        }
        return line.toString();
    }

    public static void main(String[] args) {
        Graph graph = new Graph(6);

        graph.addUndirectedEdge(1, 2);
        graph.addUndirectedEdge(0, 3);
        graph.addUndirectedEdge(2, 0);
        graph.addUndirectedEdge(5, 4);

        List<List<Integer>> components = findComponents(graph);
        printComponents(components);
    }
}
