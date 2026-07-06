import java.util.*;

interface Graph {
    int vertexCount();
    int weight(int u, int v);
}

class AdjacencyMatrixGraph implements Graph {
    private final int[][] matrix;

    AdjacencyMatrixGraph(int[][] matrix) {
        this.matrix = matrix;
    }

    @Override
    public int vertexCount() {
        return matrix.length;
    }

    @Override
    public int weight(int u, int v) {
        return matrix[u][v];
    }
}

class Edge {
    final int from;
    final int to;
    final int weight;

    Edge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return from + " - " + to + "\t" + weight;
    }
}

class MSTService {

    List<Edge> computeMST(Graph graph) {
        int V = graph.vertexCount();
        int[] parent = new int[V];
        int[] key = new int[V];
        boolean[] inMST = new boolean[V];

        Arrays.fill(key, Integer.MAX_VALUE);
        key[0] = 0;
        parent[0] = -1;

        for (int count = 0; count < V - 1; count++) {
            int u = minKey(key, inMST);
            inMST[u] = true;

            for (int v = 0; v < V; v++) {
                int w = graph.weight(u, v);
                if (w != 0 && !inMST[v] && w < key[v]) {
                    parent[v] = u;
                    key[v] = w;
                }
            }
        }

        return buildEdgeList(parent, graph);
    }

    private int minKey(int[] key, boolean[] inMST) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        for (int v = 0; v < key.length; v++) {
            if (!inMST[v] && key[v] < min) {
                min = key[v];
                minIndex = v;
            }
        }
        return minIndex;
    }

    private List<Edge> buildEdgeList(int[] parent, Graph graph) {
        List<Edge> edges = new ArrayList<>();
        for (int i = 1; i < parent.length; i++) {
            edges.add(new Edge(parent[i], i, graph.weight(parent[i], i)));
        }
        return edges;
    }
}

class MST {

    public static void main(String[] args) {
        Graph graph = new AdjacencyMatrixGraph(new int[][] {
            { 0, 2, 0, 6, 0 },
            { 2, 0, 3, 8, 5 },
            { 0, 3, 0, 0, 7 },
            { 6, 8, 0, 0, 9 },
            { 0, 5, 7, 9, 0 }
        });

        MSTService service = new MSTService();
        List<Edge> mst = service.computeMST(graph);

        System.out.println("Edge \tWeight");
        for (Edge edge : mst) {
            System.out.println(edge);
        }
    }
}
