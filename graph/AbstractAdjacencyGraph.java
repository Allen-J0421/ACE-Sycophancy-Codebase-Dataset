package graph;

import java.util.List;

abstract class AbstractAdjacencyGraph implements Graph {
    private final List<List<Integer>> adjacencyViews;

    AbstractAdjacencyGraph(List<List<Integer>> adjacencyViews) {
        this.adjacencyViews = adjacencyViews;
    }

    @Override
    public final int vertexCount() {
        return adjacencyViews.size();
    }

    @Override
    public final List<Integer> neighborsOf(int vertex) {
        requireVertex(vertex);
        return adjacencyViews.get(vertex);
    }

    protected final List<List<Integer>> adjacencyViews() {
        return adjacencyViews;
    }
}
