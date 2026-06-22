import java.util.List;

final class NeighborListGraph implements Graph {
    private final List<List<Neighbor>> neighborsByVertex;

    NeighborListGraph(List<List<Neighbor>> neighborsByVertex) {
        this.neighborsByVertex = List.copyOf(neighborsByVertex);
    }

    @Override
    public int vertexCount() {
        return neighborsByVertex.size();
    }

    @Override
    public List<Neighbor> neighborsOf(int vertex) {
        return neighborsByVertex.get(vertex);
    }
}
