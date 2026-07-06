import java.util.*;

interface Graph {
    int vertexCount();
    int weight(int u, int v);
}

class AdjacencyListGraph implements Graph {
    private final List<Map<Integer, Integer>> adjList;

    AdjacencyListGraph(int vertexCount) {
        adjList = new ArrayList<>(vertexCount);
        for (int i = 0; i < vertexCount; i++) {
            adjList.add(new HashMap<>());
        }
    }

    void addEdge(int u, int v, int weight) {
        adjList.get(u).put(v, weight);
        adjList.get(v).put(u, weight);
    }

    @Override
    public int vertexCount() {
        return adjList.size();
    }

    @Override
    public int weight(int u, int v) {
        return adjList.get(u).getOrDefault(v, 0);
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
        AdjacencyListGraph graph = new AdjacencyListGraph(5);
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 3, 6);
        graph.addEdge(1, 2, 3);
        graph.addEdge(1, 3, 8);
        graph.addEdge(1, 4, 5);
        graph.addEdge(2, 4, 7);
        graph.addEdge(3, 4, 9);

        MSTService service = new MSTService();
        List<Edge> mst = service.computeMST(graph);

        System.out.println("Edge \tWeight");
        for (Edge edge : mst) {
            System.out.println(edge);
        }
    }
}
