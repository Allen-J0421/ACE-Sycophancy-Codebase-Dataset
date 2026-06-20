import java.util.ArrayList;

class ArticulationPoints {
    static Graph buildGraph(int vertexCount, int[][] edges) {
        return Graph.fromEdges(vertexCount, edges);
    }

    static ArrayList<Integer> articulationPoints(Graph graph) {
        return graph.analyze(new ArticulationPointAnalysis());
    }

    public static void main(String[] args) {
        int vertexCount = 5;
        int[][] edges = {{0, 1}, {1, 4}, {2, 3}, {2, 4}, {3, 4}};

        Graph graph = buildGraph(vertexCount, edges);
        ArrayList<Integer> articulationPoints = articulationPoints(graph);

        for (int vertex : articulationPoints) {
            System.out.print(vertex + " ");
        }
        System.out.println();
    }
}
