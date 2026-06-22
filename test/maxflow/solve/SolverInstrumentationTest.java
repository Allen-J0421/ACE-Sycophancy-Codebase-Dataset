package maxflow.solve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import maxflow.graph.Capacity;
import maxflow.graph.FlowNetwork;
import maxflow.path.AugmentingPath;
import maxflow.path.BreadthFirstPathFinder;
import maxflow.path.CapacityScalingPathFinder;
import maxflow.path.DepthFirstPathFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Solver instrumentation")
class SolverInstrumentationTest {

    private static FlowNetwork classicNetwork() {
        return FlowNetwork.fromMatrix(new int[][] {
                {0, 16, 13, 0, 0, 0},
                {0, 0, 10, 12, 0, 0},
                {0, 4, 0, 0, 14, 0},
                {0, 0, 9, 0, 0, 20},
                {0, 0, 0, 7, 0, 4},
                {0, 0, 0, 0, 0, 0},
        });
    }

    @Test
    @DisplayName("counts one augmentation per augmenting path the solver applies")
    void countsAugmentations() {
        AugmentationCounter counter = new AugmentationCounter();
        List<AugmentingPath> observed = new ArrayList<>();
        SolveListener recording = observed::add;

        // Compose the counter with a recording listener so we can cross-check the count.
        SolveListener both = path -> {
            counter.onAugmentingPath(path);
            recording.onAugmentingPath(path);
        };

        MaxFlowResult result = new FordFulkersonSolver(new BreadthFirstPathFinder())
                .solve(classicNetwork(), 0, 5, both);

        assertEquals(Capacity.of(23), result.value());
        assertTrue(counter.count() > 0, "a non-trivial flow needs at least one augmentation");
        assertEquals(observed.size(), counter.count(), "count must match the paths reported");
        for (AugmentingPath path : observed) {
            assertTrue(path.bottleneck().isPositive(), "each reported path carries flow");
        }
    }

    @Test
    @DisplayName("reports no augmentations when no flow is possible")
    void noAugmentationsForZeroFlow() {
        FlowNetwork disconnected = FlowNetwork.builder(3).addEdge(0, 1, 5).build();
        AugmentationCounter counter = new AugmentationCounter();

        MaxFlowResult result = new FordFulkersonSolver().solve(disconnected, 0, 2, counter);

        assertEquals(Capacity.ZERO, result.value());
        assertEquals(0, counter.count());
    }

    @Test
    @DisplayName("a counter accumulates across the solves it observes")
    void counterAccumulatesAcrossSolves() {
        AugmentationCounter counter = new AugmentationCounter();
        FordFulkersonSolver solver = new FordFulkersonSolver();

        solver.solve(classicNetwork(), 0, 5, counter);
        long afterFirst = counter.count();
        solver.solve(classicNetwork(), 0, 5, counter);

        assertEquals(2 * afterFirst, counter.count(), "second solve adds the same number again");
    }

    @Test
    @DisplayName("strategies agree on the flow but generally differ in augmentation count")
    void strategiesProduceComparableCounts() {
        // A layered, capacity-varied network where capacity scaling needs fewer augmentations.
        FlowNetwork network = layered();
        int sink = network.vertexCount() - 1;

        long bfs = augmentations(new BreadthFirstPathFinder(), network, sink);
        long dfs = augmentations(new DepthFirstPathFinder(), network, sink);
        long scaling = augmentations(new CapacityScalingPathFinder(), network, sink);

        Capacity flow = new FordFulkersonSolver(new BreadthFirstPathFinder()).solve(network, 0, sink).value();
        for (long count : List.of(bfs, dfs, scaling)) {
            assertTrue(count > 0, "every strategy performs at least one augmentation");
        }
        // The whole point of capacity scaling: at most as many augmentations as plain BFS/DFS here.
        assertTrue(scaling <= bfs, "capacity scaling should not exceed BFS on this network");
        assertTrue(scaling <= dfs, "capacity scaling should not exceed DFS on this network");
        assertEquals(flow,
                new FordFulkersonSolver(new CapacityScalingPathFinder()).solve(network, 0, sink).value(),
                "all strategies compute the same maximum flow");
    }

    @Test
    @DisplayName("the listener overload validates its argument")
    void rejectsNullListener() {
        assertThrows(NullPointerException.class,
                () -> new FordFulkersonSolver().solve(classicNetwork(), 0, 5, null));
    }

    @Test
    @DisplayName("the no-op listener and the default interface method ignore events")
    void noOpListenerIgnoresEvents() {
        // SolveListener.NONE is a real listener that simply does nothing.
        MaxFlowResult viaNone =
                new FordFulkersonSolver().solve(classicNetwork(), 0, 5, SolveListener.NONE);
        assertEquals(Capacity.of(23), viaNone.value());

        // A solver that does not override the instrumented overload falls back to the plain solve.
        MaxFlowSolver plain = (network, source, sink) ->
                new FordFulkersonSolver().solve(network, source, sink);
        AugmentationCounter counter = new AugmentationCounter();
        MaxFlowResult viaDefault = plain.solve(classicNetwork(), 0, 5, counter);
        assertEquals(Capacity.of(23), viaDefault.value());
        assertEquals(0, counter.count(), "the default method does not invoke the listener");
    }

    @Test
    @DisplayName("NONE is a singleton no-op")
    void noneIsSingleton() {
        assertSame(SolveListener.NONE, SolveListener.NONE);
    }

    private static long augmentations(maxflow.path.AugmentingPathFinder finder, FlowNetwork network, int sink) {
        AugmentationCounter counter = new AugmentationCounter();
        new FordFulkersonSolver(finder).solve(network, 0, sink, counter);
        return counter.count();
    }

    private static FlowNetwork layered() {
        // width 5, depth 5 — matches StrategyComparison's regime.
        int width = 5;
        int depth = 5;
        int vertexCount = 2 + width * depth;
        int sink = vertexCount - 1;
        FlowNetwork.Builder builder = FlowNetwork.builder(vertexCount);
        for (int j = 0; j < width; j++) {
            builder.addEdge(0, 1 + j, 5 + (j * 7) % 11);
            builder.addEdge(1 + (depth - 1) * width + j, sink, 5 + (j * 5) % 11);
        }
        for (int layer = 0; layer < depth - 1; layer++) {
            for (int j = 0; j < width; j++) {
                for (int k = 0; k < width; k++) {
                    builder.addEdge(1 + layer * width + j, 1 + (layer + 1) * width + k,
                            1 + (j * 3 + k * 5 + layer * 2) % 9);
                }
            }
        }
        return builder.build();
    }
}
