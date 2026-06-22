import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class MST {
    private static final int NO_EDGE = 0;

    private MST() {
    }

    private record Edge(int from, int to, int weight) {
    }

    private record MstResult(List<Edge> edges) {
    }

    public static void main(String[] args) {
        int[][] graph = sampleGraph();
        MstResult result = computeMinimumSpanningTree(graph);
        printResult(result);
    }

    static MstResult computeMinimumSpanningTree(int[][] graph) {
        validateGraph(graph);

        int vertexCount = graph.length;
        int[] parent = new int[vertexCount];
        int[] minEdgeWeight = new int[vertexCount];
        boolean[] includedInTree = new boolean[vertexCount];

        Arrays.fill(parent, -1);
        Arrays.fill(minEdgeWeight, Integer.MAX_VALUE);
        minEdgeWeight[0] = 0;

        for (int visitedVertices = 0; visitedVertices < vertexCount; visitedVertices++) {
            int currentVertex = selectNextVertex(minEdgeWeight, includedInTree);
            if (currentVertex == -1) {
                throw new IllegalStateException("Graph must be connected.");
            }

            includedInTree[currentVertex] = true;
            updateAdjacentVertices(graph, currentVertex, parent, minEdgeWeight, includedInTree);
        }

        return buildResult(graph, parent);
    }

    private static int selectNextVertex(int[] minEdgeWeight, boolean[] includedInTree) {
        int bestWeight = Integer.MAX_VALUE;
        int bestVertex = -1;

        for (int vertex = 0; vertex < includedInTree.length; vertex++) {
            if (!includedInTree[vertex] && minEdgeWeight[vertex] < bestWeight) {
                bestWeight = minEdgeWeight[vertex];
                bestVertex = vertex;
            }
        }

        return bestVertex;
    }

    private static void updateAdjacentVertices(
            int[][] graph,
            int currentVertex,
            int[] parent,
            int[] minEdgeWeight,
            boolean[] includedInTree) {
        for (int candidateVertex = 0; candidateVertex < graph.length; candidateVertex++) {
            int edgeWeight = graph[currentVertex][candidateVertex];
            if (hasUsableEdge(edgeWeight)
                    && !includedInTree[candidateVertex]
                    && edgeWeight < minEdgeWeight[candidateVertex]) {
                parent[candidateVertex] = currentVertex;
                minEdgeWeight[candidateVertex] = edgeWeight;
            }
        }
    }

    private static MstResult buildResult(int[][] graph, int[] parent) {
        List<Edge> edges = new ArrayList<>();

        for (int vertex = 1; vertex < graph.length; vertex++) {
            int fromVertex = parent[vertex];
            if (fromVertex == -1) {
                throw new IllegalStateException("Graph must be connected.");
            }

            edges.add(new Edge(fromVertex, vertex, graph[fromVertex][vertex]));
        }

        return new MstResult(edges);
    }

    private static boolean hasUsableEdge(int edgeWeight) {
        return edgeWeight != NO_EDGE;
    }

    private static void validateGraph(int[][] graph) {
        if (graph == null || graph.length == 0) {
            throw new IllegalArgumentException("Graph must contain at least one vertex.");
        }

        for (int row = 0; row < graph.length; row++) {
            if (graph[row] == null || graph[row].length != graph.length) {
                throw new IllegalArgumentException("Graph must be a square adjacency matrix.");
            }
        }

        for (int row = 0; row < graph.length; row++) {
            if (graph[row][row] != NO_EDGE) {
                throw new IllegalArgumentException("Graph diagonal must be zero.");
            }

            for (int column = row + 1; column < graph.length; column++) {
                if (graph[row][column] != graph[column][row]) {
                    throw new IllegalArgumentException("Graph must be undirected and symmetric.");
                }
            }
        }
    }

    private static void printResult(MstResult result) {
        System.out.println("Edge \tWeight");
        for (Edge edge : result.edges()) {
            System.out.println(edge.from() + " - " + edge.to() + "\t" + edge.weight());
        }
    }

    private static int[][] sampleGraph() {
        return new int[][] {
            {0, 2, 0, 6, 0},
            {2, 0, 3, 8, 5},
            {0, 3, 0, 0, 7},
            {6, 8, 0, 0, 9},
            {0, 5, 7, 9, 0}
        };
    }
}
