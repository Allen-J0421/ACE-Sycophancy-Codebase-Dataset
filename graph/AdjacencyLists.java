package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class AdjacencyLists {
    private final List<List<Integer>> mutableNeighbors;
    private final List<List<Integer>> neighborViews;

    private AdjacencyLists(int vertexCount) {
        mutableNeighbors = new ArrayList<>(vertexCount);
        neighborViews = new ArrayList<>(vertexCount);

        for (int vertex = 0; vertex < vertexCount; vertex++) {
            List<Integer> neighbors = new ArrayList<>();
            mutableNeighbors.add(neighbors);
            neighborViews.add(Collections.unmodifiableList(neighbors));
        }
    }

    static AdjacencyLists withVertices(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("Vertex count cannot be negative.");
        }
        return new AdjacencyLists(vertexCount);
    }

    List<List<Integer>> views() {
        return neighborViews;
    }

    void addDirectedEdge(int from, int to) {
        mutableNeighbors.get(from).add(to);
    }

    List<List<Integer>> snapshotViews() {
        List<List<Integer>> snapshot = new ArrayList<>(mutableNeighbors.size());
        for (List<Integer> neighbors : mutableNeighbors) {
            snapshot.add(List.copyOf(neighbors));
        }
        return Collections.unmodifiableList(snapshot);
    }
}
