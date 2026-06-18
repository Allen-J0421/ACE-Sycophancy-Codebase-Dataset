package graph.algorithm;

import graph.model.IDirectedGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class StronglyConnectedComponents {
    private final IDirectedGraph graph;
    private boolean[] visited;
    private final Stack<Integer> stack;
    private final int[] sccId;

    public StronglyConnectedComponents(IDirectedGraph graph) {
        this.graph = graph;
        this.visited = new boolean[graph.getVertexCount()];
        this.stack = new Stack<>();
        this.sccId = new int[graph.getVertexCount()];
        findSCCs();
    }

    private void findSCCs() {
        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                fillOrder(i);
            }
        }

        IDirectedGraph reversed = graph.reverse();
        visited = new boolean[graph.getVertexCount()];
        int componentCount = 0;

        while (!stack.isEmpty()) {
            int v = stack.pop();
            if (!visited[v]) {
                dfsSCC(reversed, v, componentCount);
                componentCount++;
            }
        }
    }

    private void fillOrder(int vertex) {
        visited[vertex] = true;
        for (int neighbor : graph.getOutgoing(vertex)) {
            if (!visited[neighbor]) {
                fillOrder(neighbor);
            }
        }
        stack.push(vertex);
    }

    private void dfsSCC(IDirectedGraph graph, int vertex, int componentId) {
        visited[vertex] = true;
        sccId[vertex] = componentId;
        for (int neighbor : graph.getOutgoing(vertex)) {
            if (!visited[neighbor]) {
                dfsSCC(graph, neighbor, componentId);
            }
        }
    }

    public List<List<Integer>> getSCCs() {
        int maxSccId = 0;
        for (int id : sccId) {
            maxSccId = Math.max(maxSccId, id);
        }

        List<List<Integer>> sccs = new ArrayList<>();
        for (int i = 0; i <= maxSccId; i++) {
            sccs.add(new ArrayList<>());
        }

        for (int i = 0; i < sccId.length; i++) {
            sccs.get(sccId[i]).add(i);
        }

        return sccs;
    }

    public int getComponentCount() {
        int maxId = 0;
        for (int id : sccId) {
            maxId = Math.max(maxId, id);
        }
        return maxId + 1;
    }

    public boolean isSCC(int u, int v) {
        return sccId[u] == sccId[v];
    }
}
