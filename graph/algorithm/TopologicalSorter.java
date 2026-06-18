package graph.algorithm;

import graph.model.IDirectedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TopologicalSorter {
    private final IDirectedGraph graph;
    private final boolean[] visited;
    private final Stack<Integer> stack;

    public TopologicalSorter(IDirectedGraph graph) {
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
        for (int neighbor : graph.getOutgoing(vertex)) {
            if (!visited[neighbor]) {
                dfs(neighbor);
            }
        }
        stack.push(vertex);
    }

    public List<Integer> sortKahn() {
        int[] inDegree = new int[graph.getVertexCount()];
        for (int i = 0; i < graph.getVertexCount(); i++) {
            inDegree[i] = graph.getInDegree(i);
        }

        List<Integer> result = new ArrayList<>();
        java.util.Queue<Integer> queue = new java.util.LinkedList<>();

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        while (!queue.isEmpty()) {
            int u = queue.poll();
            result.add(u);

            for (int v : graph.getOutgoing(u)) {
                inDegree[v]--;
                if (inDegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }

        if (result.size() != graph.getVertexCount()) {
            throw new IllegalArgumentException("Graph contains a cycle");
        }

        return result;
    }
}
