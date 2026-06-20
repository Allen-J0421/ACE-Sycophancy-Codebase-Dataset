package bipartite;

import java.util.Collections;
import java.util.List;

/**
 * The outcome of a bipartite check.
 *
 * <p>When {@link #isBipartite()} is {@code true}, {@link #partitionA()} and
 * {@link #partitionB()} hold the two independent vertex sets of a valid
 * 2-coloring. When it is {@code false}, both partitions are empty because no
 * such 2-coloring exists.
 */
public final class BipartiteResult {

    private static final BipartiteResult NOT_BIPARTITE =
            new BipartiteResult(false, List.of(), List.of());

    private final boolean bipartite;
    private final List<Integer> partitionA;
    private final List<Integer> partitionB;

    private BipartiteResult(boolean bipartite, List<Integer> partitionA, List<Integer> partitionB) {
        this.bipartite = bipartite;
        this.partitionA = partitionA;
        this.partitionB = partitionB;
    }

    /** @return a result for a graph that is bipartite, with its two vertex sets */
    static BipartiteResult bipartite(List<Integer> partitionA, List<Integer> partitionB) {
        return new BipartiteResult(
                true,
                Collections.unmodifiableList(partitionA),
                Collections.unmodifiableList(partitionB));
    }

    /** @return the shared result for a graph that is not bipartite */
    static BipartiteResult notBipartite() {
        return NOT_BIPARTITE;
    }

    /** @return whether the graph can be 2-colored */
    public boolean isBipartite() {
        return bipartite;
    }

    /** @return the vertices assigned the first color (empty if not bipartite) */
    public List<Integer> partitionA() {
        return partitionA;
    }

    /** @return the vertices assigned the second color (empty if not bipartite) */
    public List<Integer> partitionB() {
        return partitionB;
    }

    @Override
    public String toString() {
        if (!bipartite) {
            return "BipartiteResult{bipartite=false}";
        }
        return "BipartiteResult{bipartite=true, partitionA=" + partitionA
                + ", partitionB=" + partitionB + "}";
    }
}
