import java.util.ArrayList;
import java.util.List;

final class NeighborListGraph implements Graph {
    private final List<List<Neighbor>> neighborsByVertex;

    private NeighborListGraph(List<List<Neighbor>> neighborsByVertex) {
        this.neighborsByVertex = copyNeighbors(neighborsByVertex);
    }

    static Graph fromAdjacencyMatrix(AdjacencyMatrix adjacencyMatrix) {
        List<List<Neighbor>> neighborsByVertex = new ArrayList<>(adjacencyMatrix.vertexCount());

        for (int vertex = 0; vertex < adjacencyMatrix.vertexCount(); vertex++) {
            neighborsByVertex.add(adjacencyMatrix.neighborsOf(vertex));
        }

        return new NeighborListGraph(neighborsByVertex);
    }

    @Override
    public int vertexCount() {
        return neighborsByVertex.size();
    }

    @Override
    public List<Neighbor> neighborsOf(int vertex) {
        return neighborsByVertex.get(vertex);
    }

    private static List<List<Neighbor>> copyNeighbors(List<List<Neighbor>> neighborsByVertex) {
        List<List<Neighbor>> copiedNeighbors = new ArrayList<>(neighborsByVertex.size());

        for (List<Neighbor> neighbors : neighborsByVertex) {
            copiedNeighbors.add(List.copyOf(neighbors));
        }

        return List.copyOf(copiedNeighbors);
    }
}
