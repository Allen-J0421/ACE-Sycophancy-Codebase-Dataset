final class BellmanFordDemo {
    private BellmanFordDemo() {
    }

    public static void main(String[] args) {
        BellmanFordFixtures.Case sampleCase = BellmanFordFixtures.sampleCase();

        ShortestPathResult result = BellmanFord.computeShortestPaths(
            sampleCase.graph(),
            sampleCase.source()
        );
        if (result.hasNegativeCycle()) {
            System.out.println("Negative cycle detected");
            return;
        }

        System.out.println(result);
    }
}
