import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class KosarajuStronglyConnectedComponents implements StronglyConnectedComponentsFinder {

    @Override
    public StronglyConnectedComponentsResult find(DirectedGraph graph) {
        int n = graph.vertexCount();
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
                components.add(new StronglyConnectedComponent(collectComponent(u, reversed, visited)));
            }
        }

        return new StronglyConnectedComponentsResult(components);
    }

    private void collectFinishOrder(int start, DirectedGraph graph, boolean[] visited, Deque<Integer> finishOrder) {
        Deque<int[]> callStack = new ArrayDeque<>();
        visited[start] = true;
        callStack.push(new int[]{start, 0});

        while (!callStack.isEmpty()) {
            int[] frame = callStack.peek();
            int u = frame[0];
            List<Integer> nbrs = graph.neighbors(u);

            boolean descended = false;
            while (frame[1] < nbrs.size()) {
                int v = nbrs.get(frame[1]++);
                if (!visited[v]) {
                    visited[v] = true;
                    callStack.push(new int[]{v, 0});
                    descended = true;
                    break;
                }
            }

            if (!descended) {
                callStack.pop();
                finishOrder.push(u);
            }
        }
    }

    private List<Integer> collectComponent(int start, DirectedGraph reversed, boolean[] visited) {
        List<Integer> vertices = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        visited[start] = true;
        stack.push(start);

        while (!stack.isEmpty()) {
            int u = stack.pop();
            vertices.add(u);
            for (int v : reversed.neighbors(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    stack.push(v);
                }
            }
        }
        return vertices;
    }
}
