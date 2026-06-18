package graph;

import java.util.List;

final class AdjacencyListGraph implements Graph {
    private final List<List<Integer>> adjacencyViews;

    AdjacencyListGraph(List<List<Integer>> adjacencyViews) {
        this.adjacencyViews = adjacencyViews;
    }

    @Override
    public int vertexCount() {
        return adjacencyViews.size();
    }

    @Override
    public List<Integer> neighborsOf(int vertex) {
        requireVertex(vertex);
        return adjacencyViews.get(vertex);
    }
}
