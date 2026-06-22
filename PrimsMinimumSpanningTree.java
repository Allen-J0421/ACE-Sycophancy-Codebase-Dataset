final class PrimsMinimumSpanningTree implements MinimumSpanningTreeAlgorithm {
    private static final int START_VERTEX = 0;

    @Override
    public MstResult compute(Graph graph) {
        PrimVertexState vertexState = new PrimVertexState(graph.vertexCount(), START_VERTEX);

        for (int visitedVertices = 0; visitedVertices < graph.vertexCount(); visitedVertices++) {
            int currentVertex = vertexState.nextVertexToInclude();
            if (currentVertex == -1) {
                throw new IllegalStateException("Graph must be connected.");
            }

            vertexState.include(currentVertex);
            relaxAdjacentVertices(graph, currentVertex, vertexState);
        }

        return vertexState.buildResult();
    }

    private static void relaxAdjacentVertices(Graph graph, int currentVertex, PrimVertexState vertexState) {
        for (Neighbor neighbor : graph.neighborsOf(currentVertex)) {
            int candidateVertex = neighbor.vertex();
            if (vertexState.isIncluded(candidateVertex)) {
                continue;
            }

            vertexState.considerEdge(currentVertex, candidateVertex, neighbor.weight());
        }
    }
}
