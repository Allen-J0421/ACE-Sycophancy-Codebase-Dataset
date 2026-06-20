public final class BipartiteGraphDemo {
    private BipartiteGraphDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        int vertexCount = 4;
        int[][] edges = {{0, 1}, {0, 2}, {1, 2}, {2, 3}};

        System.out.println(BipartiteGraph.isBipartite(vertexCount, edges));
    }
}
