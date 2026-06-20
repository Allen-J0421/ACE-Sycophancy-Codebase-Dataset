import java.util.Arrays;

class MST {
    private static final int NO_EDGE = 0;
    private static final int ROOT_PARENT = -1;
    private static final int START_VERTEX = 0;

    private static int findNearestUnvisitedVertex(int[] minimumEdgeWeight, boolean[] includedInMst) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int v = 0; v < includedInMst.length; v++) {
            if (!includedInMst[v] && minimumEdgeWeight[v] < min) {
                min = minimumEdgeWeight[v];
                minIndex = v;
            }
        }

        return minIndex;
    }

    private static void printMinimumSpanningTree(int[] parent, int[][] graph) {
        System.out.println("Edge \tWeight");
        for (int i = START_VERTEX + 1; i < graph.length; i++) {
            System.out.println(parent[i] + " - " + i + "\t"
                               + graph[parent[i]][i]);
        }
    }

    private static boolean hasEdge(int weight) {
        return weight != NO_EDGE;
    }

    private static boolean isBetterConnection(
        int from,
        int to,
        int[][] graph,
        int[] minimumEdgeWeight,
        boolean[] includedInMst
    ) {
        int edgeWeight = graph[from][to];
        return hasEdge(edgeWeight) && !includedInMst[to]
               && edgeWeight < minimumEdgeWeight[to];
    }

    private static void updateAdjacentVertices(
        int vertex,
        int[][] graph,
        int[] parent,
        int[] minimumEdgeWeight,
        boolean[] includedInMst
    ) {
        for (int candidate = 0; candidate < graph.length; candidate++) {
            if (isBetterConnection(vertex, candidate, graph, minimumEdgeWeight, includedInMst)) {
                parent[candidate] = vertex;
                minimumEdgeWeight[candidate] = graph[vertex][candidate];
            }
        }
    }

    private static int[] buildMinimumSpanningTree(int[][] graph) {
        int vertexCount = graph.length;
        int[] parent = new int[vertexCount];
        int[] minimumEdgeWeight = new int[vertexCount];
        boolean[] includedInMst = new boolean[vertexCount];

        Arrays.fill(minimumEdgeWeight, Integer.MAX_VALUE);
        minimumEdgeWeight[START_VERTEX] = 0;
        parent[START_VERTEX] = ROOT_PARENT;

        for (int count = 0; count < vertexCount - 1; count++) {
            int vertex = findNearestUnvisitedVertex(minimumEdgeWeight, includedInMst);
            includedInMst[vertex] = true;
            updateAdjacentVertices(vertex, graph, parent, minimumEdgeWeight, includedInMst);
        }

        return parent;
    }

    private static void printPrimMinimumSpanningTree(int[][] graph) {
        int[] parent = buildMinimumSpanningTree(graph);
        printMinimumSpanningTree(parent, graph);
    }

    private static int[][] sampleGraph() {
        return new int[][] {
            { NO_EDGE, 2, NO_EDGE, 6, NO_EDGE },
            { 2, NO_EDGE, 3, 8, 5 },
            { NO_EDGE, 3, NO_EDGE, NO_EDGE, 7 },
            { 6, 8, NO_EDGE, NO_EDGE, 9 },
            { NO_EDGE, 5, 7, 9, NO_EDGE }
        };
    }

    public static void main(String[] args) {
        printPrimMinimumSpanningTree(sampleGraph());
    }
}
