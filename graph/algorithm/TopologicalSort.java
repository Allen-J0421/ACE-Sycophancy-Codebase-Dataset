package graph.algorithm;

import graph.core.IGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TopologicalSort {
    private final IGraph graph;
    private final boolean[] visited;
    private final Stack<Integer> stack;

    public TopologicalSort(IGraph graph) {
        this.graph = graph;
        this.visited = new boolean[graph.getVertexCount()];
        this.stack = new Stack<>();
    }

    public List<Integer> sort() {
        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                dfs(i);
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
        return result;
    }

    private void dfs(int vertex) {
        visited[vertex] = true;

        for (int neighbor : graph.getNeighbors(vertex)) {
            if (!visited[neighbor]) {
                dfs(neighbor);
            }
        }

        stack.push(vertex);
    }
}
