package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An undirected graph backed by an adjacency list.
 *
 * <p>Vertices are identified by the integers {@code 0 .. vertexCount() - 1}.
 * The adjacency list is not exposed directly; callers read neighbours through
 * {@link #neighbours(int)}, which returns an unmodifiable view.
 */
public final class Graph {

    private final List<List<Integer>> adjacency;

    /**
     * Creates a graph with the given number of vertices and no edges.
     *
     * @throws IllegalArgumentException if {@code vertexCount} is negative
     */
    public Graph(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }
        adjacency = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    /** Returns the number of vertices in the graph. */
    public int vertexCount() {
        return adjacency.size();
    }

    /**
     * Adds an undirected edge between {@code u} and {@code v}.
     *
     * @throws IndexOutOfBoundsException if either endpoint is not a valid vertex
     */
    public void addEdge(int u, int v) {
        adjacency.get(checkVertex(u)).add(v);
        adjacency.get(checkVertex(v)).add(u);
    }

    /** Returns an unmodifiable view of the neighbours of {@code vertex}. */
    public List<Integer> neighbours(int vertex) {
        return Collections.unmodifiableList(adjacency.get(checkVertex(vertex)));
    }

    private int checkVertex(int vertex) {
        return Objects.checkIndex(vertex, adjacency.size());
    }
}
