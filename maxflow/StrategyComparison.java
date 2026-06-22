package maxflow;

import java.util.LinkedHashMap;
import java.util.Map;

import maxflow.graph.FlowNetwork;
import maxflow.path.AugmentingPathFinder;
import maxflow.path.BreadthFirstPathFinder;
import maxflow.path.CapacityScalingPathFinder;
import maxflow.path.DepthFirstPathFinder;
import maxflow.solve.AugmentationCounter;
import maxflow.solve.FordFulkersonSolver;
import maxflow.solve.MaxFlowProblem;
import maxflow.solve.MaxFlowResult;

/**
 * Runs each augmenting-path strategy on the same network and reports how many augmenting
 * paths each had to discover. This is the empirical efficiency comparison the
 * {@link maxflow.solve.SolveListener} instrumentation makes possible: every strategy
 * computes the same maximum flow, but the number of augmentations differs — and on a
 * layered network with widely varying capacities, capacity scaling needs far fewer.
 */
public final class StrategyComparison {

    private StrategyComparison() {
    }

    public static void main(String[] args) {
        FlowNetwork network = layeredNetwork(5, 5);
        MaxFlowProblem problem = new MaxFlowProblem(network, 0, network.vertexCount() - 1);

        Map<String, AugmentingPathFinder> strategies = new LinkedHashMap<>();
        strategies.put("BFS (Edmonds-Karp)", new BreadthFirstPathFinder());
        strategies.put("DFS (Ford-Fulkerson)", new DepthFirstPathFinder());
        strategies.put("Capacity scaling", new CapacityScalingPathFinder());

        System.out.printf("Layered network: %d vertices, source %d, sink %d%n%n",
                network.vertexCount(), problem.source(), problem.sink());
        System.out.printf("%-24s %10s %14s%n", "strategy", "max flow", "augmentations");
        strategies.forEach((label, finder) -> {
            AugmentationCounter counter = new AugmentationCounter();
            MaxFlowResult result = new FordFulkersonSolver(finder).solve(problem, counter);
            System.out.printf("%-24s %10s %14d%n", label, result.value(), counter.count());
        });
    }

    /**
     * Builds a deterministic layered DAG: a source, {@code depth} layers of {@code width}
     * vertices fully connected to the next layer, then a sink. Capacities follow a fixed
     * arithmetic pattern so they span a wide range — the regime where the strategies
     * diverge — while keeping the result reproducible.
     */
    private static FlowNetwork layeredNetwork(int width, int depth) {
        int vertexCount = 2 + width * depth;
        int source = 0;
        int sink = vertexCount - 1;
        FlowNetwork.Builder builder = FlowNetwork.builder(vertexCount);

        for (int j = 0; j < width; j++) {
            builder.addEdge(source, vertexAt(0, j, width), 5 + (j * 7) % 11);
            builder.addEdge(vertexAt(depth - 1, j, width), sink, 5 + (j * 5) % 11);
        }
        for (int layer = 0; layer < depth - 1; layer++) {
            for (int j = 0; j < width; j++) {
                for (int k = 0; k < width; k++) {
                    builder.addEdge(vertexAt(layer, j, width), vertexAt(layer + 1, k, width),
                            1 + (j * 3 + k * 5 + layer * 2) % 9);
                }
            }
        }
        return builder.build();
    }

    private static int vertexAt(int layer, int column, int width) {
        return 1 + layer * width + column;
    }
}
