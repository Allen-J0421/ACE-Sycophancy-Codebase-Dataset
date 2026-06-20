import java.util.ArrayList;
import java.util.List;

final class Graph {
    private final List<List<Integer>> adjacencyList;

    private Graph(List<List<Integer>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    static Graph fromEdges(int vertexCount, int[][] edges) {
        List<List<Integer>> adjacencyList = new ArrayList<>();
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            adjacencyList.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            adjacencyList.get(from).add(to);
            adjacencyList.get(to).add(from);
        }

        return new Graph(adjacencyList);
    }

    int vertexCount() {
        return adjacencyList.size();
    }

    List<Integer> neighborsOf(int vertex) {
        return adjacencyList.get(vertex);
    }

    <Result> Result analyze(GraphAnalysis<Result> analysis) {
        return analysis.analyze(this);
    }
}
