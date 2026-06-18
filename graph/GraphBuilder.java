package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GraphBuilder extends AbstractAdjacencyGraph {
    private final List<List<Integer>> adjacencyList;

    private GraphBuilder(int vertexCount) {
        super(createAdjacencyViews(vertexCount));
        adjacencyList = new ArrayList<>(vertexCount);
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            List<Integer> neighbors = new ArrayList<>();
            adjacencyList.add(neighbors);
            adjacencyViews().add(Collections.unmodifiableList(neighbors));
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
        List<List<Integer>> adjacencyViews = new ArrayList<>(vertexCount());
        for (List<Integer> neighbors : adjacencyList) {
            adjacencyViews.add(List.copyOf(neighbors));
        }
        return new AdjacencyListGraph(Collections.unmodifiableList(adjacencyViews));
    }

    private static List<List<Integer>> createAdjacencyViews(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }
        return new ArrayList<>(vertexCount);
    }
}
