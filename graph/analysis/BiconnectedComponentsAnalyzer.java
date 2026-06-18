package graph.analysis;

import graph.core.IGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BiconnectedComponentsAnalyzer {
    private final IGraph graph;
    private final boolean[] visited;
    private final int[] disc;
    private final int[] low;
    private final int[] parent;
    private int time;
    private final Stack<Integer> stack;
    private final List<List<Integer>> components;

    public BiconnectedComponentsAnalyzer(IGraph graph) {
        this.graph = graph;
        this.visited = new boolean[graph.getVertexCount()];
        this.disc = new int[graph.getVertexCount()];
        this.low = new int[graph.getVertexCount()];
        this.parent = new int[graph.getVertexCount()];
        this.time = 0;
        this.stack = new Stack<>();
        this.components = new ArrayList<>();

        for (int i = 0; i < graph.getVertexCount(); i++) {
            parent[i] = -1;
        }

        analyzeComponents();
    }

    private void analyzeComponents() {
        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                dfs(i);
            }
        }
    }

    private void dfs(int u) {
        visited[u] = true;
        disc[u] = low[u] = time++;
        int children = 0;

        for (int v : graph.getNeighbors(u)) {
            if (!visited[v]) {
                children++;
                parent[v] = u;
                stack.push(u);
                stack.push(v);
                dfs(v);

                if ((parent[u] == -1 && children > 1) || (parent[u] != -1 && low[v] >= disc[u])) {
                    List<Integer> component = new ArrayList<>();
                    while (true) {
                        int edge = stack.pop();
                        if (component.isEmpty() || !contains(component, edge)) {
                            component.add(edge);
                        }
                        if (stack.isEmpty()) break;
                        int node = stack.pop();
                        if (component.isEmpty() || !contains(component, node)) {
                            component.add(node);
                        }
                        if (node == u && edge == v) break;
                    }
                    if (!component.isEmpty()) {
                        components.add(component);
                    }
                }

                low[u] = Math.min(low[u], low[v]);
            } else if (v != parent[u]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    private boolean contains(List<Integer> list, int value) {
        for (int v : list) {
            if (v == value) return true;
        }
        return false;
    }

    public List<List<Integer>> getComponents() {
        return components;
    }

    public int getComponentCount() {
        return components.size();
    }

    public List<Integer> getArticulationPoints() {
        List<Integer> points = new ArrayList<>();
        for (int u = 0; u < graph.getVertexCount(); u++) {
            if (parent[u] == -1) {
                int children = 0;
                for (int v : graph.getNeighbors(u)) {
                    if (parent[v] == u) children++;
                }
                if (children > 1) points.add(u);
            } else {
                boolean isArticulation = false;
                for (int v : graph.getNeighbors(u)) {
                    if (parent[v] == u && low[v] >= disc[u]) {
                        isArticulation = true;
                        break;
                    }
                }
                if (isArticulation) points.add(u);
            }
        }
        return points;
    }
}
