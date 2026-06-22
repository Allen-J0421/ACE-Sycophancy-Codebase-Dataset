import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class PrimsMST implements MstAlgorithm {

    @Override
    public MstResult computeMST(Graph graph) {
        return computeMST(graph, 0);
    }

    @Override
    public MstResult computeMST(Graph graph, int startVertex) {
        int n = graph.vertexCount();
        if (startVertex < 0 || startVertex >= n) {
            throw new IllegalArgumentException(
                "Start vertex " + startVertex + " is out of range [0, " + (n - 1) + "]");
        }

        VertexState state = new VertexState(n);
        state.setSource(startVertex);

        for (int count = 0; count < n - 1; count++) {
            int u = state.nextVertex();
            if (u == -1) {
                throw new IllegalArgumentException("Graph is disconnected; MST does not exist");
            }
            state.include(u);

            for (int v = 0; v < n; v++) {
                state.relaxEdge(u, v, graph.weight(u, v));
            }
        }

        return new MstResult(state.buildEdges());
    }

    public static void main(String[] args) {
        int[][] matrix = {
            { 0, 2, 0, 6, 0 },
            { 2, 0, 3, 8, 5 },
            { 0, 3, 0, 0, 7 },
            { 6, 8, 0, 0, 9 },
            { 0, 5, 7, 9, 0 }
        };

        MstAlgorithm algorithm = new PrimsMST();
        MstResult result = algorithm.computeMST(Graph.fromMatrix(matrix));
        result.print();
    }

    private static class VertexState {
        private final int[] parent;
        private final int[] minEdgeWeight;
        private final boolean[] inMST;

        VertexState(int vertexCount) {
            parent = new int[vertexCount];
            minEdgeWeight = new int[vertexCount];
            inMST = new boolean[vertexCount];
            Arrays.fill(minEdgeWeight, Integer.MAX_VALUE);
        }

        void setSource(int vertex) {
            minEdgeWeight[vertex] = 0;
            parent[vertex] = -1;
        }

        int nextVertex() {
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

        void include(int vertex) {
            inMST[vertex] = true;
        }

        void relaxEdge(int from, int to, int weight) {
            if (weight != 0 && !inMST[to] && weight < minEdgeWeight[to]) {
                parent[to] = from;
                minEdgeWeight[to] = weight;
            }
        }

        List<Edge> buildEdges() {
            List<Edge> edges = new ArrayList<>();
            for (int i = 1; i < parent.length; i++) {
                edges.add(new Edge(parent[i], i, minEdgeWeight[i]));
            }
            return edges;
        }
    }
}
