final class BellmanFordDemo {
    private BellmanFordDemo() {
    }

    public static void main(String[] args) {
        WeightedGraph graph = BellmanFordFixtures.sampleGraph();
        int source = 0;

        ShortestPathResult result = BellmanFord.computeShortestPaths(graph, source);
        if (result.hasNegativeCycle()) {
            System.out.println("Negative cycle detected");
            return;
        }

        System.out.println(result);
    }
}
