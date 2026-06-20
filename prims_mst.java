import java.util.Arrays;

class MST {
    private static final int NO_EDGE = 0;
    private static final int ROOT_PARENT = -1;
    private static final int START_VERTEX = 0;

    private static final class Graph {
        private final int[][] weights;

        private Graph(int[][] weights) {
            this.weights = weights;
        }

        private int vertexCount() {
            return weights.length;
        }

        private int weight(int from, int to) {
            return weights[from][to];
        }

        private boolean hasEdge(int from, int to) {
            return weight(from, to) != NO_EDGE;
        }
    }

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

    private static void printMinimumSpanningTree(int[] parent, Graph graph) {
        System.out.println("Edge \tWeight");
        for (int i = START_VERTEX + 1; i < graph.vertexCount(); i++) {
            System.out.println(parent[i] + " - " + i + "\t"
                               + graph.weight(parent[i], i));
        }
    }

    private static boolean isBetterConnection(
        int from,
        int to,
        Graph graph,
        PrimState state
    ) {
        int edgeWeight = graph.weight(from, to);
        return graph.hasEdge(from, to) && !state.includedInMst[to]
               && edgeWeight < state.minimumEdgeWeight[to];
    }

    private static void updateAdjacentVertices(
        int vertex,
        Graph graph,
        PrimState state
    ) {
        for (int candidate = 0; candidate < graph.vertexCount(); candidate++) {
            if (isBetterConnection(vertex, candidate, graph, state)) {
                state.parent[candidate] = vertex;
                state.minimumEdgeWeight[candidate] = graph.weight(vertex, candidate);
            }
        }
    }

    private static int[] buildMinimumSpanningTree(Graph graph) {
        PrimState state = new PrimState(graph.vertexCount());

        for (int count = 0; count < state.vertexCount() - 1; count++) {
            int vertex = findNearestUnvisitedVertex(state);
            state.includedInMst[vertex] = true;
            updateAdjacentVertices(vertex, graph, state);
        }

        return state.parent;
    }

    private static void printPrimMinimumSpanningTree(Graph graph) {
        int[] parent = buildMinimumSpanningTree(graph);
        printMinimumSpanningTree(parent, graph);
    }

    private static Graph sampleGraph() {
        return new Graph(new int[][] {
            { NO_EDGE, 2, NO_EDGE, 6, NO_EDGE },
            { 2, NO_EDGE, 3, 8, 5 },
            { NO_EDGE, 3, NO_EDGE, NO_EDGE, 7 },
            { 6, 8, NO_EDGE, NO_EDGE, 9 },
            { NO_EDGE, 5, 7, 9, NO_EDGE }
        });
    }

    public static void main(String[] args) {
        printPrimMinimumSpanningTree(sampleGraph());
    }
}
