package bipartite;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Computes the strongly connected components (SCCs) of a {@link Graph} using
 * Tarjan's algorithm: a single depth-first search that assigns each vertex a
 * discovery index and a "low-link" — the smallest index reachable from it —
 * emitting a component whenever a vertex's low-link equals its own index.
 *
 * <p>The search is iterative, simulating recursion with an explicit work stack,
 * so it handles deep graphs without risking a stack overflow — the same
 * robustness choice as the other algorithms in this package. It depends only on
 * the {@link Graph} interface, treating {@link Graph#neighbors(int)} as outgoing
 * edges, so it runs on any implementation; SCCs are most meaningful for directed
 * graphs, while for an undirected graph they coincide with its connected
 * components. The computation runs in {@code O(V + E)} time and {@code O(V)} space.
 */
public final class StronglyConnectedComponents {

    private static final int UNVISITED = -1;

    /**
     * Computes the strongly connected components of the given graph.
     *
     * @param graph the graph to analyse (any {@link Graph} implementation)
     * @return a {@link StronglyConnectedComponentsResult.StronglyConnected} if the
     *         whole graph is one component, otherwise a
     *         {@link StronglyConnectedComponentsResult.Fragmented}
     * @throws NullPointerException if {@code graph} is null
     */
    public StronglyConnectedComponentsResult compute(Graph graph) {
        if (graph == null) {
            throw new NullPointerException("graph must not be null");
        }

        int order = graph.order();
        int[] index = new int[order];
        int[] lowLink = new int[order];
        boolean[] onStack = new boolean[order];
        Arrays.fill(index, UNVISITED);

        Deque<Integer> tarjanStack = new ArrayDeque<>();
        List<List<Integer>> components = new ArrayList<>();
        int[] counter = {0};

        for (int vertex = 0; vertex < order; vertex++) {
            if (index[vertex] == UNVISITED) {
                strongConnect(graph, vertex, index, lowLink, onStack, tarjanStack, components, counter);
            }
        }

        if (components.size() == 1) {
            return new StronglyConnectedComponentsResult.StronglyConnected(components.get(0));
        }
        return new StronglyConnectedComponentsResult.Fragmented(components);
    }

    /**
     * Iterative Tarjan depth-first search rooted at {@code start}. Each work-stack
     * frame is {@code {vertex, nextNeighborIndex}}; a frame is revisited to resume
     * scanning a vertex's neighbors after a child has been fully explored, at
     * which point the child's low-link is folded into the parent's.
     */
    private static void strongConnect(Graph graph, int start, int[] index, int[] lowLink,
                                      boolean[] onStack, Deque<Integer> tarjanStack,
                                      List<List<Integer>> components, int[] counter) {
        Deque<int[]> work = new ArrayDeque<>();
        work.push(new int[] {start, 0});

        while (!work.isEmpty()) {
            int[] frame = work.peek();
            int u = frame[0];

            if (frame[1] == 0) {
                index[u] = lowLink[u] = counter[0]++;
                tarjanStack.push(u);
                onStack[u] = true;
            }

            boolean descended = false;
            List<Integer> neighbors = graph.neighbors(u);
            for (int j = frame[1]; j < neighbors.size(); j++) {
                int v = neighbors.get(j);
                if (index[v] == UNVISITED) {
                    frame[1] = j + 1;            // resume after this neighbor
                    work.push(new int[] {v, 0}); // "recurse" into v
                    descended = true;
                    break;
                } else if (onStack[v]) {
                    lowLink[u] = Math.min(lowLink[u], index[v]);
                }
            }
            if (descended) {
                continue;
            }

            if (lowLink[u] == index[u]) {
                components.add(popComponent(tarjanStack, onStack, u));
            }

            work.pop();
            if (!work.isEmpty()) {
                int parent = work.peek()[0];
                lowLink[parent] = Math.min(lowLink[parent], lowLink[u]);
            }
        }
    }

    /** Pops the SCC rooted at {@code root} off the Tarjan stack, sorted ascending. */
    private static List<Integer> popComponent(Deque<Integer> tarjanStack, boolean[] onStack, int root) {
        List<Integer> component = new ArrayList<>();
        int vertex;
        do {
            vertex = tarjanStack.pop();
            onStack[vertex] = false;
            component.add(vertex);
        } while (vertex != root);
        Collections.sort(component);
        return component;
    }
}
