package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GraphBuilder implements Graph {
    private final List<List<Integer>> adjacencyList;
    private final List<List<Integer>> adjacencyViews;

    private GraphBuilder(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }

        adjacencyList = new ArrayList<>(vertexCount);
        adjacencyViews = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            List<Integer> neighbors = new ArrayList<>();
            adjacencyList.add(neighbors);
            adjacencyViews.add(Collections.unmodifiableList(neighbors));
        }
    }

    public static GraphBuilder withVertices(int vertexCount) {
        return new GraphBuilder(vertexCount);
    }

    @Override
    public int vertexCount() {
        return adjacencyList.size();
    }

    @Override
    public List<Integer> neighborsOf(int vertex) {
        requireVertex(vertex);
        return adjacencyViews.get(vertex);
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
}
