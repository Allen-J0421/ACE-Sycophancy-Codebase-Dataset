import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Kruskal's algorithm for the Minimum Spanning Tree (MST) of an undirected,
 * weighted graph.
 *
 * <p>The class is split into focused pieces:
 * <ul>
 *   <li>{@link Edge} — an immutable weighted edge.</li>
 *   <li>{@link DisjointSet} — union-find with path compression and union by rank.</li>
 *   <li>{@link MstResult} — the chosen edges plus total weight and connectivity.</li>
 * </ul>
 *
 * <p>{@link #findMst(int, List)} returns the full result. {@link #mstWeight(int, int[][])}
 * is a thin, backward-compatible wrapper that returns only the total weight.
 */
final class KruskalMST {

    private KruskalMST() {
        // Utility class: not instantiable.
    }

    /**
     * Computes the minimum spanning tree (or minimum spanning forest, if the
     * graph is disconnected) using Kruskal's algorithm.
     *
     * @param vertexCount number of vertices, labelled {@code 0 .. vertexCount - 1}
     * @param edges       the undirected edges of the graph (not mutated)
     * @return the selected edges, their total weight, and whether they span the graph
     * @throws IllegalArgumentException if {@code vertexCount} is negative or an
     *                                  edge references a vertex outside the valid range
     */
    public static MstResult findMst(int vertexCount, List<Edge> edges) {
        if (vertexCount < 0) {
            throw new IllegalArgumentException("vertexCount must be non-negative: " + vertexCount);
        }
        Objects.requireNonNull(edges, "edges");

        // Sort a copy so the caller's collection is left untouched.
        List<Edge> sorted = new ArrayList<>(edges);
        for (Edge e : sorted) {
            e.requireValidVertices(vertexCount);
        }
        Collections.sort(sorted);

        DisjointSet components = new DisjointSet(vertexCount);
        List<Edge> chosen = new ArrayList<>(Math.max(0, vertexCount - 1));
        long totalWeight = 0;

        for (Edge edge : sorted) {
            // Adding an edge whose endpoints already share a component would
            // create a cycle, so only union endpoints that are still separate.
            if (components.union(edge.source(), edge.destination())) {
                chosen.add(edge);
                totalWeight += edge.weight();
                if (chosen.size() == vertexCount - 1) {
                    break; // A spanning tree always has exactly vertexCount - 1 edges.
                }
            }
        }

        boolean spanning = vertexCount == 0 || chosen.size() == vertexCount - 1;
        return new MstResult(Collections.unmodifiableList(chosen), totalWeight, spanning);
    }

    /**
     * Backward-compatible convenience method: builds the MST from an edge array
     * where each row is {@code {source, destination, weight}} and returns only
     * the total weight.
     *
     * @param vertexCount number of vertices
     * @param edges       rows of {@code {source, destination, weight}}
     * @return the total weight of the minimum spanning tree (or forest)
     */
    public static int mstWeight(int vertexCount, int[][] edges) {
        return (int) findMst(vertexCount, toEdgeList(edges)).totalWeight();
    }

    private static List<Edge> toEdgeList(int[][] rows) {
        Objects.requireNonNull(rows, "edges");
        List<Edge> edges = new ArrayList<>(rows.length);
        for (int[] row : rows) {
            if (row == null || row.length != 3) {
                throw new IllegalArgumentException(
                        "each edge must be {source, destination, weight}, got: " + Arrays.toString(row));
            }
            edges.add(new Edge(row[0], row[1], row[2]));
        }
        return edges;
    }

    public static void main(String[] args) {
        int[][] edges = {
            {0, 1, 10}, {1, 3, 15}, {2, 3, 4}, {2, 0, 6}, {0, 3, 5}
        };

        MstResult result = findMst(4, toEdgeList(edges));
        System.out.println("Total weight: " + result.totalWeight());
        System.out.println("Spanning:     " + result.spanning());
        System.out.println("Edges:        " + result.edges());
    }
}

/**
 * An immutable, undirected weighted edge. Ordered by weight so a natural sort
 * yields the order Kruskal's algorithm consumes edges in.
 */
record Edge(int source, int destination, int weight) implements Comparable<Edge> {

    @Override
    public int compareTo(Edge other) {
        return Integer.compare(this.weight, other.weight);
    }

    void requireValidVertices(int vertexCount) {
        if (source < 0 || source >= vertexCount || destination < 0 || destination >= vertexCount) {
            throw new IllegalArgumentException(
                    "edge " + this + " references a vertex outside [0, " + vertexCount + ")");
        }
    }
}

/**
 * The outcome of running Kruskal's algorithm.
 *
 * @param edges       the edges selected for the tree (unmodifiable)
 * @param totalWeight the sum of the selected edge weights
 * @param spanning    {@code true} if the edges connect every vertex into a
 *                    single tree; {@code false} for a disconnected graph, where
 *                    the result is a minimum spanning forest
 */
record MstResult(List<Edge> edges, long totalWeight, boolean spanning) {
}

/**
 * Disjoint-set (union-find) structure with path compression and union by rank,
 * giving near-constant amortised time per operation.
 */
final class DisjointSet {
    private final int[] parent;
    private final int[] rank;

    DisjointSet(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("size must be non-negative: " + size);
        }
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    /**
     * Returns the representative of {@code i}'s set, compressing the path along
     * the way. Iterative (path halving) so it cannot overflow the stack on
     * deep chains, while keeping the near-constant amortised cost of compression.
     */
    int find(int i) {
        while (parent[i] != i) {
            parent[i] = parent[parent[i]]; // point i at its grandparent
            i = parent[i];
        }
        return i;
    }

    /** Returns {@code true} if {@code x} and {@code y} already belong to the same set. */
    boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    /**
     * Merges the sets containing {@code x} and {@code y}.
     *
     * @return {@code true} if a merge happened, {@code false} if they were already joined
     */
    boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) {
            return false;
        }
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        return true;
    }
}
