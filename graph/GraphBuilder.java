package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GraphBuilder {
    private final List<List<Integer>> adjacencyList;

    private GraphBuilder(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }

        adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    public static GraphBuilder withVertices(int vertexCount) {
        return new GraphBuilder(vertexCount);
    }

    public GraphBuilder addDirectedEdge(int from, int to) {
        requireVertex(from);
        requireVertex(to);
        adjacencyList.get(from).add(to);
        return this;
    }

    public GraphBuilder addUndirectedEdge(int from, int to) {
        return addDirectedEdge(from, to).addDirectedEdge(to, from);
    }

    public Graph build() {
        List<List<Integer>> adjacencyViews = new ArrayList<>(adjacencyList.size());
        for (List<Integer> neighbors : adjacencyList) {
            adjacencyViews.add(List.copyOf(neighbors));
        }
        return new AdjacencyListGraph(Collections.unmodifiableList(adjacencyViews));
    }

    private void requireVertex(int vertex) {
        if (vertex < 0 || vertex >= adjacencyList.size()) {
            throw new IllegalArgumentException("Vertex out of range: " + vertex);
        }
    }
}
