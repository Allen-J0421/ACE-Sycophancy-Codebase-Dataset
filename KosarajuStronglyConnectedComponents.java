import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class KosarajuStronglyConnectedComponents implements StronglyConnectedComponentsFinder {

    @Override
    public StronglyConnectedComponentsResult find(DirectedGraph graph) {
        int n = graph.getVertexCount();
        boolean[] visited = new boolean[n + 1];
        Deque<Integer> finishOrder = new ArrayDeque<>();

        for (int i = 1; i <= n; i++) {
            if (!visited[i]) {
                collectFinishOrder(i, graph, visited, finishOrder);
            }
        }

        DirectedGraph reversed = graph.reverse();
        Arrays.fill(visited, false);

        List<StronglyConnectedComponent> components = new ArrayList<>();
        while (!finishOrder.isEmpty()) {
            int u = finishOrder.pop();
            if (!visited[u]) {
                List<Integer> vertices = new ArrayList<>();
                collectComponent(u, reversed, visited, vertices);
                components.add(new StronglyConnectedComponent(vertices));
            }
        }

        return new StronglyConnectedComponentsResult(components);
    }

    private void collectFinishOrder(int u, DirectedGraph graph, boolean[] visited, Deque<Integer> finishOrder) {
        visited[u] = true;
        for (int v : graph.neighbors(u)) {
            if (!visited[v]) {
                collectFinishOrder(v, graph, visited, finishOrder);
            }
        }
        finishOrder.push(u);
    }

    private void collectComponent(int u, DirectedGraph reversed, boolean[] visited, List<Integer> vertices) {
        visited[u] = true;
        vertices.add(u);
        for (int v : reversed.neighbors(u)) {
            if (!visited[v]) {
                collectComponent(v, reversed, visited, vertices);
            }
        }
    }
}
