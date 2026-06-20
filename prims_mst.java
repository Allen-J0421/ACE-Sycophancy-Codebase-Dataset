import java.util.Arrays;

class MST {
    private static final int NO_EDGE = 0;
    private static final int ROOT_PARENT = -1;
    private static final int START_VERTEX = 0;

    private static final class PrimState {
        private final int[] parent;
        private final int[] minimumEdgeWeight;
        private final boolean[] includedInMst;

        private PrimState(int vertexCount) {
            parent = new int[vertexCount];
            minimumEdgeWeight = new int[vertexCount];
            includedInMst = new boolean[vertexCount];

            Arrays.fill(minimumEdgeWeight, Integer.MAX_VALUE);
            minimumEdgeWeight[START_VERTEX] = 0;
            parent[START_VERTEX] = ROOT_PARENT;
        }

        private int vertexCount() {
            return parent.length;
        }
    }

    private static int findNearestUnvisitedVertex(PrimState state) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int v = 0; v < state.vertexCount(); v++) {
            if (!state.includedInMst[v] && state.minimumEdgeWeight[v] < min) {
                min = state.minimumEdgeWeight[v];
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
        PrimState state
    ) {
        int edgeWeight = graph[from][to];
        return hasEdge(edgeWeight) && !state.includedInMst[to]
               && edgeWeight < state.minimumEdgeWeight[to];
    }

    private static void updateAdjacentVertices(
        int vertex,
        int[][] graph,
        PrimState state
    ) {
        for (int candidate = 0; candidate < graph.length; candidate++) {
            if (isBetterConnection(vertex, candidate, graph, state)) {
                state.parent[candidate] = vertex;
                state.minimumEdgeWeight[candidate] = graph[vertex][candidate];
            }
        }
    }

    private static int[] buildMinimumSpanningTree(int[][] graph) {
        PrimState state = new PrimState(graph.length);

        for (int count = 0; count < state.vertexCount() - 1; count++) {
            int vertex = findNearestUnvisitedVertex(state);
            state.includedInMst[vertex] = true;
            updateAdjacentVertices(vertex, graph, state);
        }

        return state.parent;
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
