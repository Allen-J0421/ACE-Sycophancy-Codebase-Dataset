import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class DirectedGraphBuilder {
    private final List<List<Integer>> adjacencyList;
    private final int[] indegree;

    DirectedGraphBuilder(int vertexCount) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative");
        }

        adjacencyList = new ArrayList<>(vertexCount);
        indegree = new int[vertexCount];
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }
    }

    DirectedGraphBuilder addEdge(int source, int destination) {
        DirectedGraph.validateVertexIndex(source, adjacencyList.size());
        DirectedGraph.validateVertexIndex(destination, adjacencyList.size());
        adjacencyList.get(source).add(destination);
        indegree[destination]++;
        return this;
    }

    DirectedGraph build() {
        List<List<Integer>> immutableAdjacencyList = new ArrayList<>(adjacencyList.size());
        for (List<Integer> neighbors : adjacencyList) {
            immutableAdjacencyList.add(Collections.unmodifiableList(new ArrayList<>(neighbors)));
        }

        return new DirectedGraph(
                Collections.unmodifiableList(immutableAdjacencyList),
                indegree.clone());
    }
}
