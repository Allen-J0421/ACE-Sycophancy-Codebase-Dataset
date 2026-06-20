import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private static final class MstEdge {
        private final int from;
        private final int to;
        private final int weight;

        private MstEdge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        private String format() {
            return from + " - " + to + "\t" + weight;
        }
    }

    private static final class PrimAlgorithm {
        private final Graph graph;
        private final PrimState state;

        private PrimAlgorithm(Graph graph) {
            this.graph = graph;
            state = new PrimState(graph.vertexCount());
        }

        private int[] buildMinimumSpanningTree() {
            for (int count = 0; count < state.vertexCount() - 1; count++) {
                int vertex = findNearestUnvisitedVertex();
                state.includedInMst[vertex] = true;
                updateAdjacentVertices(vertex);
            }

            return state.parent;
        }

        private int findNearestUnvisitedVertex() {
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

        private void updateAdjacentVertices(int vertex) {
            for (int candidate = 0; candidate < graph.vertexCount(); candidate++) {
                if (isBetterConnection(vertex, candidate)) {
                    state.parent[candidate] = vertex;
                    state.minimumEdgeWeight[candidate] = graph.weight(vertex, candidate);
                }
            }
        }

        private boolean isBetterConnection(int from, int to) {
            int edgeWeight = graph.weight(from, to);
            return graph.hasEdge(from, to) && !state.includedInMst[to]
                   && edgeWeight < state.minimumEdgeWeight[to];
        }
    }

    private static List<MstEdge> buildMinimumSpanningTreeEdges(int[] parent, Graph graph) {
        List<MstEdge> edges = new ArrayList<>();

        for (int vertex = START_VERTEX + 1; vertex < graph.vertexCount(); vertex++) {
            int source = parent[vertex];
            edges.add(new MstEdge(source, vertex, graph.weight(source, vertex)));
        }

        return edges;
    }

    private static void printMinimumSpanningTree(List<MstEdge> edges) {
        System.out.println("Edge \tWeight");
        for (MstEdge edge : edges) {
            System.out.println(edge.format());
        }
    }

    private static int[] buildMinimumSpanningTree(Graph graph) {
        return new PrimAlgorithm(graph).buildMinimumSpanningTree();
    }

    private static void printPrimMinimumSpanningTree(Graph graph) {
        int[] parent = buildMinimumSpanningTree(graph);
        List<MstEdge> edges = buildMinimumSpanningTreeEdges(parent, graph);
        printMinimumSpanningTree(edges);
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
