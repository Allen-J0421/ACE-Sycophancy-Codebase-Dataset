package bipartite;

/**
 * Small demonstration of the bipartite checker. Mirrors the original example:
 * a 4-vertex graph containing the triangle 0-1-2, which is <em>not</em>
 * bipartite because of that odd cycle.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        UndirectedGraph graph = UndirectedGraph.of(4, new int[][] {
                {0, 1}, {0, 2}, {1, 2}, {2, 3}
        });

        BipartiteResult result = new BipartiteChecker().check(graph);

        System.out.println("Bipartite: " + result.isBipartite());
        switch (result) {
            case BipartiteResult.Bipartite bipartite -> {
                System.out.println("Partition A: " + bipartite.partitionA());
                System.out.println("Partition B: " + bipartite.partitionB());
            }
            case BipartiteResult.NotBipartite notBipartite ->
                    System.out.println("Odd-cycle witness: " + notBipartite.oddCycle());
        }
    }
}
