import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * Cycle detection via Kahn's algorithm: repeatedly remove vertices whose
 * in-degree has dropped to zero. A graph is acyclic exactly when every vertex
 * can be removed this way; any vertices that remain lie on (or feed into) a cycle.
 *
 * <p>Existence is therefore a cheap by-product of the elimination pass, so this
 * implementation overrides {@link #hasCycle}. Reconstructing an actual cycle
 * needs a little more work (see {@link #findCycle}) but stays O(V + E).
 */
final class KahnCycleDetector implements CycleDetector {

    @Override
    public String name() {
        return "KAHN";
    }

    @Override
    public boolean hasCycle(DirectedGraph graph) {
        int vertexCount = graph.vertices();
        int[] inDegree = inDegrees(graph);

        Deque<Integer> ready = readyQueue(inDegree);
        int removed = 0;
        while (!ready.isEmpty()) {
            int u = ready.poll();
            removed++;
            for (int v : graph.neighbors(u)) {
                if (--inDegree[v] == 0) {
                    ready.add(v);
                }
            }
        }
        return removed != vertexCount;
    }

    @Override
    public Optional<Cycle> findCycle(DirectedGraph graph) {
        int vertexCount = graph.vertices();
        boolean[] onCycleCore = residualCore(graph);

        int start = -1;
        for (int v = 0; v < vertexCount; v++) {
            if (onCycleCore[v]) {
                start = v;
                break;
            }
        }
        if (start < 0) {
            return Optional.empty();
        }

        // Every core vertex has an out-edge to another core vertex, so walking
        // them must revisit a vertex; the segment between the two visits is a cycle.
        int[] position = new int[vertexCount];
        Arrays.fill(position, -1);
        List<Integer> walk = new ArrayList<>();
        int current = start;
        while (position[current] < 0) {
            position[current] = walk.size();
            walk.add(current);
            current = firstCoreNeighbor(graph, current, onCycleCore);
        }

        List<Integer> cycle = new ArrayList<>(walk.subList(position[current], walk.size()));
        cycle.add(current);
        return Optional.of(new Cycle(cycle));
    }

    /**
     * Returns the "cycle core": vertices that survive Kahn elimination and still
     * have an out-edge into the surviving set. Every such vertex is reachable
     * along core edges to a cycle, and following any core out-edge stays in the core.
     */
    private boolean[] residualCore(DirectedGraph graph) {
        int vertexCount = graph.vertices();
        int[] inDegree = inDegrees(graph);

        Deque<Integer> ready = readyQueue(inDegree);
        boolean[] core = new boolean[vertexCount];
        Arrays.fill(core, true);
        while (!ready.isEmpty()) {
            int u = ready.poll();
            core[u] = false; // u is part of a valid topological prefix, not a cycle
            for (int v : graph.neighbors(u)) {
                if (--inDegree[v] == 0) {
                    ready.add(v);
                }
            }
        }

        // Peel survivors that lead nowhere within the core: they cannot lie on a cycle.
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int u = 0; u < vertexCount; u++) {
                if (core[u] && firstCoreNeighbor(graph, u, core) < 0) {
                    core[u] = false;
                    changed = true;
                }
            }
        }
        return core;
    }

    private static int firstCoreNeighbor(DirectedGraph graph, int u, boolean[] core) {
        for (int v : graph.neighbors(u)) {
            if (core[v]) {
                return v;
            }
        }
        return -1;
    }

    private static int[] inDegrees(DirectedGraph graph) {
        int vertexCount = graph.vertices();
        int[] inDegree = new int[vertexCount];
        for (int u = 0; u < vertexCount; u++) {
            for (int v : graph.neighbors(u)) {
                inDegree[v]++;
            }
        }
        return inDegree;
    }

    private static Deque<Integer> readyQueue(int[] inDegree) {
        Deque<Integer> ready = new ArrayDeque<>();
        for (int v = 0; v < inDegree.length; v++) {
            if (inDegree[v] == 0) {
                ready.add(v);
            }
        }
        return ready;
    }
}
