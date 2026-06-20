package bipartite;

import java.util.List;

/**
 * The outcome of a bipartite check, modelled as a sealed type so that callers
 * handle exactly the two possible cases and illegal states cannot be
 * represented.
 *
 * <p>Either the graph {@linkplain Bipartite is bipartite}, in which case the
 * two independent vertex sets of a valid 2-coloring are available, or it
 * {@linkplain NotBipartite is not}, in which case an odd-length cycle witnessing
 * the failure is available. Callers can branch with a switch:
 *
 * <pre>{@code
 * switch (checker.check(graph)) {
 *     case BipartiteResult.Bipartite b    -> use(b.partitionA(), b.partitionB());
 *     case BipartiteResult.NotBipartite n -> report(n.oddCycle());
 * }
 * }</pre>
 */
public sealed interface BipartiteResult
        permits BipartiteResult.Bipartite, BipartiteResult.NotBipartite {

    /** @return whether the graph can be 2-colored */
    boolean isBipartite();

    /**
     * Result for a graph that admits a 2-coloring.
     *
     * @param partitionA the vertices assigned the first color
     * @param partitionB the vertices assigned the second color
     */
    record Bipartite(List<Integer> partitionA, List<Integer> partitionB)
            implements BipartiteResult {

        public Bipartite {
            partitionA = List.copyOf(partitionA);
            partitionB = List.copyOf(partitionB);
        }

        @Override
        public boolean isBipartite() {
            return true;
        }
    }

    /**
     * Result for a graph that is not bipartite, carrying a witness.
     *
     * @param oddCycle vertices forming an odd-length cycle (the closing edge
     *                 runs from the last vertex back to the first); its presence
     *                 proves the graph cannot be 2-colored
     */
    record NotBipartite(List<Integer> oddCycle) implements BipartiteResult {

        public NotBipartite {
            oddCycle = List.copyOf(oddCycle);
        }

        @Override
        public boolean isBipartite() {
            return false;
        }
    }
}
