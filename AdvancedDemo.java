import graph.algorithm.CycleFinder;
import graph.algorithm.TopologicalSort;
import graph.analysis.BipartiteChecker;
import graph.analysis.GraphComparator;
import graph.core.Graph;
import graph.core.GraphBuilder;
import graph.core.ImmutableGraph;
import graph.io.GraphFormat;
import graph.io.GraphSerializer;
import graph.metrics.GraphMetrics;

public class AdvancedDemo {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║         Advanced Graph Framework Demonstration             ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        demonstrateImmutability();
        demonstrateSerialization();
        demonstrateMetrics();
        demonstrateCycleFinding();
        demonstrateBipartite();
        demonstrateComparison();
    }

    private static void demonstrateImmutability() {
        System.out.println("─── IMMUTABILITY DEMONSTRATION ───\n");

        Graph mutable = new GraphBuilder(5)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 3)
                .addEdge(3, 4)
                .build();

        ImmutableGraph immutable = new ImmutableGraph(mutable);

        System.out.println("Created immutable graph from mutable graph");
        System.out.println("Vertices: " + immutable.getVertexCount());
        System.out.println("Edges: " + immutable.getEdgeCount());

        try {
            immutable.addEdge(0, 4);
            System.out.println("ERROR: Should not allow modification");
        } catch (UnsupportedOperationException e) {
            System.out.println("✓ Immutable graph correctly rejected modification\n");
        }
    }

    private static void demonstrateSerialization() {
        System.out.println("─── SERIALIZATION DEMONSTRATION ───\n");

        Graph graph = new GraphBuilder(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 3)
                .addEdge(3, 0)
                .build();

        System.out.println("Graph serialized as ADJACENCY LIST:\n");
        System.out.println(GraphSerializer.serialize(graph, GraphFormat.ADJACENCY_LIST));

        System.out.println("Graph serialized as EDGE LIST:\n");
        System.out.println(GraphSerializer.serialize(graph, GraphFormat.EDGE_LIST));

        System.out.println("Graph serialized as ADJACENCY MATRIX:\n");
        System.out.println(GraphSerializer.serialize(graph, GraphFormat.MATRIX));
    }

    private static void demonstrateMetrics() {
        System.out.println("─── GRAPH METRICS DEMONSTRATION ───\n");

        Graph graph = new GraphBuilder(6)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 3)
                .addEdge(3, 4)
                .addEdge(4, 5)
                .addEdge(5, 0)
                .build();

        GraphMetrics metrics = new GraphMetrics(graph);
        System.out.println(metrics.getReport());
    }

    private static void demonstrateCycleFinding() {
        System.out.println("─── CYCLE FINDING DEMONSTRATION ───\n");

        Graph cyclicGraph = new GraphBuilder(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 3)
                .addEdge(3, 0)
                .build();

        CycleFinder cyclicFinder = new CycleFinder(cyclicGraph);
        System.out.println("Cyclic Graph (0-1-2-3-0):");
        System.out.println("  Has Cycle: " + cyclicFinder.hasCycle());
        System.out.println("  Found cycles: " + cyclicFinder.findAllCycles().size());

        Graph acyclicGraph = new GraphBuilder(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 3)
                .build();

        CycleFinder acyclicFinder = new CycleFinder(acyclicGraph);
        System.out.println("\nAcyclic Graph (0-1-2-3):");
        System.out.println("  Has Cycle: " + acyclicFinder.hasCycle());
        System.out.println("  Found cycles: " + acyclicFinder.findAllCycles().size() + "\n");
    }

    private static void demonstrateBipartite() {
        System.out.println("─── BIPARTITE CHECKING DEMONSTRATION ───\n");

        Graph bipartiteGraph = new GraphBuilder(6)
                .addEdge(0, 1)
                .addEdge(0, 3)
                .addEdge(2, 1)
                .addEdge(2, 4)
                .addEdge(4, 5)
                .build();

        BipartiteChecker checker = new BipartiteChecker(bipartiteGraph);
        System.out.println("Bipartite Graph Test:");
        System.out.println("  Is Bipartite: " + checker.isBipartite());
        if (checker.isBipartite()) {
            System.out.println("  Partition 0: " + checker.getPartition(0));
            System.out.println("  Partition 1: " + checker.getPartition(1));
        }

        Graph nonBipartiteGraph = new GraphBuilder(3)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 0)
                .build();

        BipartiteChecker tripleChecker = new BipartiteChecker(nonBipartiteGraph);
        System.out.println("\nNon-Bipartite Graph (Triangle):");
        System.out.println("  Is Bipartite: " + tripleChecker.isBipartite() + "\n");
    }

    private static void demonstrateComparison() {
        System.out.println("─── GRAPH COMPARISON DEMONSTRATION ───\n");

        Graph g1 = new GraphBuilder(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 3)
                .build();

        Graph g2 = new GraphBuilder(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .addEdge(2, 3)
                .build();

        System.out.println("Graph Equality:");
        System.out.println("  G1 equals G2: " + GraphComparator.isEqual(g1, g2));

        System.out.println("\nGraph Isomorphism (degree sequence):");
        System.out.println("  G1 isomorphic to G2: " + GraphComparator.isIsomorphic(g1, g2));

        Graph g3 = new GraphBuilder(4)
                .addEdge(0, 1)
                .addEdge(1, 2)
                .build();

        System.out.println("\nJaccard Similarity:");
        double similarity = GraphComparator.jaccardSimilarity(g1, g3);
        System.out.println("  G1 vs G3: " + String.format("%.4f", similarity) + "\n");
    }
}
