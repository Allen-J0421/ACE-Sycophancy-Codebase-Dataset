import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class PrimsMinimumSpanningTree implements MinimumSpanningTreeAlgorithm {
    @Override
    public MstResult compute(Graph graph) {
        int vertexCount = graph.vertexCount();
        int[] parentByVertex = new int[vertexCount];
        int[] bestWeightByVertex = new int[vertexCount];
        boolean[] includedInTree = new boolean[vertexCount];

        Arrays.fill(parentByVertex, -1);
        Arrays.fill(bestWeightByVertex, Integer.MAX_VALUE);
        bestWeightByVertex[0] = 0;

        for (int visitedVertices = 0; visitedVertices < vertexCount; visitedVertices++) {
            int currentVertex = selectNextVertex(bestWeightByVertex, includedInTree);
            if (currentVertex == -1) {
                throw new IllegalStateException("Graph must be connected.");
            }

            includedInTree[currentVertex] = true;
            relaxAdjacentVertices(graph, currentVertex, parentByVertex, bestWeightByVertex, includedInTree);
        }

        return buildResult(parentByVertex, bestWeightByVertex);
    }

    private static int selectNextVertex(int[] bestWeightByVertex, boolean[] includedInTree) {
        int lightestWeight = Integer.MAX_VALUE;
        int selectedVertex = -1;

        for (int vertex = 0; vertex < includedInTree.length; vertex++) {
            if (!includedInTree[vertex] && bestWeightByVertex[vertex] < lightestWeight) {
                lightestWeight = bestWeightByVertex[vertex];
                selectedVertex = vertex;
            }
        }

        return selectedVertex;
    }

    private static void relaxAdjacentVertices(
            Graph graph,
            int currentVertex,
            int[] parentByVertex,
            int[] bestWeightByVertex,
            boolean[] includedInTree) {
        for (Neighbor neighbor : graph.neighborsOf(currentVertex)) {
            int candidateVertex = neighbor.vertex();
            if (includedInTree[candidateVertex]) {
                continue;
            }

            int candidateWeight = neighbor.weight();
            if (candidateWeight < bestWeightByVertex[candidateVertex]) {
                parentByVertex[candidateVertex] = currentVertex;
                bestWeightByVertex[candidateVertex] = candidateWeight;
            }
        }
    }

    private static MstResult buildResult(int[] parentByVertex, int[] bestWeightByVertex) {
        List<Edge> edges = new ArrayList<>();

        for (int vertex = 1; vertex < parentByVertex.length; vertex++) {
            int parentVertex = parentByVertex[vertex];
            if (parentVertex == -1) {
                throw new IllegalStateException("Graph must be connected.");
            }

            edges.add(new Edge(parentVertex, vertex, bestWeightByVertex[vertex]));
        }

        return new MstResult(edges);
    }
}
