package graph.analysis;

import graph.core.IGraph;

import java.util.ArrayList;
import java.util.List;

public class ConnectedComponentsAnalyzer {
    private final IGraph graph;
    private final boolean[] visited;
    private final int[] componentId;
    private int componentCount;

    public ConnectedComponentsAnalyzer(IGraph graph) {
        this.graph = graph;
        this.visited = new boolean[graph.getVertexCount()];
        this.componentId = new int[graph.getVertexCount()];
    }

    public List<List<Integer>> findConnectedComponents() {
        componentCount = 0;
        List<List<Integer>> components = new ArrayList<>();

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                List<Integer> component = new ArrayList<>();
                dfs(i, componentCount, component);
                components.add(component);
                componentCount++;
            }
        }

        return components;
    }

    public int getComponentCount() {
        if (componentCount == 0) {
            findConnectedComponents();
        }
        return componentCount;
    }

    public boolean isConnected() {
        return getComponentCount() == 1;
    }

    public int getComponentOf(int vertex) {
        if (vertex < 0 || vertex >= graph.getVertexCount()) {
            throw new IndexOutOfBoundsException("Invalid vertex: " + vertex);
        }
        if (componentCount == 0) {
            findConnectedComponents();
        }
        return componentId[vertex];
    }

    private void dfs(int vertex, int currentComponent, List<Integer> component) {
        visited[vertex] = true;
        componentId[vertex] = currentComponent;
        component.add(vertex);

        for (int neighbor : graph.getNeighbors(vertex)) {
            if (!visited[neighbor]) {
                dfs(neighbor, currentComponent, component);
            }
        }
    }
}
