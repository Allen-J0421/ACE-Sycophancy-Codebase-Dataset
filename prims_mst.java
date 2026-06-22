import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;

class PrimsMST implements MstAlgorithm {

    @Override
    public MstResult computeMST(Graph graph, int startVertex) {
        if (graph == null) {
            throw new IllegalArgumentException("Graph must not be null");
        }
        int n = graph.vertexCount();
        if (startVertex < 0 || startVertex >= n) {
            throw new IllegalArgumentException(
                "Start vertex " + startVertex + " is out of range [0, " + (n - 1) + "]");
        }

        VertexState state = new VertexState(n, startVertex);

        for (int count = 0; count < n - 1; count++) {
            OptionalInt next = state.nextVertex();
            if (!next.isPresent()) {
                throw new IllegalArgumentException("Graph is disconnected; MST does not exist");
            }
            int u = next.getAsInt();
            state.include(u);

            for (Edge edge : graph.edgesFrom(u)) {
                state.relaxEdge(edge);
            }
        }

        List<Edge> mstEdges = state.buildEdges();
        if (mstEdges.size() != n - 1) {
            throw new IllegalArgumentException("Graph is disconnected; MST does not exist");
        }
        return new MstResult(mstEdges);
    }

    private static class VertexState {
        private final int[] parent;
        private final int[] minEdgeWeight;
        private final boolean[] inMST;
        private final int startVertex;

        VertexState(int vertexCount, int startVertex) {
            this.startVertex = startVertex;
            parent = new int[vertexCount];
            minEdgeWeight = new int[vertexCount];
            inMST = new boolean[vertexCount];
            Arrays.fill(minEdgeWeight, Integer.MAX_VALUE);
            minEdgeWeight[startVertex] = 0;
            parent[startVertex] = -1;
        }

        OptionalInt nextVertex() {
            int min = Integer.MAX_VALUE;
            int minVertex = -1;
            for (int v = 0; v < minEdgeWeight.length; v++) {
                if (!inMST[v] && minEdgeWeight[v] < min) {
                    min = minEdgeWeight[v];
                    minVertex = v;
                }
            }
            return minVertex == -1 ? OptionalInt.empty() : OptionalInt.of(minVertex);
        }

        void include(int vertex) {
            inMST[vertex] = true;
        }

        void relaxEdge(Edge edge) {
            int to = edge.to();
            if (!inMST[to] && edge.weight() < minEdgeWeight[to]) {
                parent[to] = edge.from();
                minEdgeWeight[to] = edge.weight();
            }
        }

        List<Edge> buildEdges() {
            List<Edge> edges = new ArrayList<>();
            for (int i = 0; i < parent.length; i++) {
                if (i != startVertex && minEdgeWeight[i] != Integer.MAX_VALUE) {
                    edges.add(new Edge(parent[i], i, minEdgeWeight[i]));
                }
            }
            return edges;
        }
    }
}
