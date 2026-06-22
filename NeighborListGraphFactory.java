final class NeighborListGraphFactory implements GraphFactory {
    @Override
    public Graph create(AdjacencyMatrix adjacencyMatrix) {
        return NeighborListGraph.fromAdjacencyMatrix(adjacencyMatrix);
    }
}
