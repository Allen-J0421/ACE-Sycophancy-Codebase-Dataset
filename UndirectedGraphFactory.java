import java.util.ArrayList;
import java.util.List;

final class UndirectedGraphFactory {

    private UndirectedGraphFactory() {
    }

    static UndirectedGraph fromEdgeList(int vertexCount, int[][] edges) {
        GraphInputValidator.validateVertexCount(vertexCount);
        GraphInputValidator.validateEdgeList(edges);

        List<List<Integer>> adjacencyList = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            GraphInputValidator.validateEdge(edge);

            int source = edge[0];
            int target = edge[1];
            GraphInputValidator.validateVertex(source, vertexCount);
            GraphInputValidator.validateVertex(target, vertexCount);

            adjacencyList.get(source).add(target);
            adjacencyList.get(target).add(source);
        }

        List<List<Integer>> frozenAdjacencyList = new ArrayList<>(vertexCount);
        for (List<Integer> neighbors : adjacencyList) {
            frozenAdjacencyList.add(List.copyOf(neighbors));
        }

        return new UndirectedGraph(vertexCount, List.copyOf(frozenAdjacencyList));
    }
}
