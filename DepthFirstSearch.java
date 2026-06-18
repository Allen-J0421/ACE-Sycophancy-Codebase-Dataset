import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringJoiner;
import java.util.ArrayList;
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

        System.out.println(formatTraversal(dfs(graph)));
    }

    public static List<Integer> dfs(Graph graph) {
        boolean[] visited = new boolean[graph.vertexCount()];
        List<Integer> traversal = new ArrayList<>();

        for (int vertex = 0; vertex < graph.vertexCount(); vertex++) {
            if (!visited[vertex]) {
                traverseComponent(graph, vertex, visited, traversal);
            }
        }

        return traversal;
    }

    public static List<Integer> dfsFrom(Graph graph, int startVertex) {
        boolean[] visited = new boolean[graph.vertexCount()];
        List<Integer> traversal = new ArrayList<>();
        traverseComponent(graph, startVertex, visited, traversal);
        return traversal;
    }

    private static void traverseComponent(Graph graph,
                                          int startVertex,
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

            List<Integer> neighbors = graph.neighborsOf(vertex);
            for (int index = neighbors.size() - 1; index >= 0; index--) {
                int neighbor = neighbors.get(index);
                if (!visited[neighbor]) {
                    stack.push(neighbor);
                }
            }
        }
    }

    private static String formatTraversal(List<Integer> traversal) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int vertex : traversal) {
            joiner.add(Integer.toString(vertex));
        }
        return joiner.toString();
    }
}
