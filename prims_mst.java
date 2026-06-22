import java.util.ArrayList;
import java.util.List;

class PrimsMST {

    List<Edge> computeMST(Graph graph) {
        int n = graph.vertexCount();
        int[] parent = new int[n];
        int[] minEdgeWeight = new int[n];
        boolean[] inMST = new boolean[n];

        for (int i = 0; i < n; i++) {
            minEdgeWeight[i] = Integer.MAX_VALUE;
        }
        minEdgeWeight[0] = 0;
        parent[0] = -1;

        for (int count = 0; count < n - 1; count++) {
            int u = findMinWeightVertex(minEdgeWeight, inMST);
            inMST[u] = true;

            for (int v = 0; v < n; v++) {
                int w = graph.weight(u, v);
                if (w != 0 && !inMST[v] && w < minEdgeWeight[v]) {
                    parent[v] = u;
                    minEdgeWeight[v] = w;
                }
            }
        }

        return buildEdges(parent, graph, n);
    }

    private int findMinWeightVertex(int[] minEdgeWeight, boolean[] inMST) {
        int min = Integer.MAX_VALUE;
        int minVertex = -1;

        for (int v = 0; v < minEdgeWeight.length; v++) {
            if (!inMST[v] && minEdgeWeight[v] < min) {
                min = minEdgeWeight[v];
                minVertex = v;
            }
        }

        return minVertex;
    }

    private List<Edge> buildEdges(int[] parent, Graph graph, int n) {
        List<Edge> edges = new ArrayList<>();
        for (int i = 1; i < n; i++) {
            edges.add(new Edge(parent[i], i, graph.weight(parent[i], i)));
        }
        return edges;
    }

    static void printMST(List<Edge> edges) {
        System.out.println("Edge \tWeight");
        for (Edge edge : edges) {
            System.out.println(edge);
        }
    }

    public static void main(String[] args) {
        int[][] matrix = {
            { 0, 2, 0, 6, 0 },
            { 2, 0, 3, 8, 5 },
            { 0, 3, 0, 0, 7 },
            { 6, 8, 0, 0, 9 },
            { 0, 5, 7, 9, 0 }
        };

        Graph graph = new Graph(matrix);
        PrimsMST solver = new PrimsMST();
        List<Edge> mst = solver.computeMST(graph);
        printMST(mst);
    }
}
