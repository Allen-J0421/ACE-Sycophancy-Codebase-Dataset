package graph;

import java.util.List;

public interface Graph {
    int vertexCount();

    List<Integer> neighborsOf(int vertex);

    default boolean containsVertex(int vertex) {
        return vertex >= 0 && vertex < vertexCount();
    }

    default void requireVertex(int vertex) {
        if (!containsVertex(vertex)) {
            throw new IllegalArgumentException("Vertex out of range: " + vertex);
        }
    }

    default boolean isEmpty() {
        return vertexCount() == 0;
    }
}
