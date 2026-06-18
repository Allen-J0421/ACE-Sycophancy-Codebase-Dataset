/**
 * Demonstrates snapshot and state-restoration functionality.
 *
 * Shows:
 * - Creating graph snapshots
 * - Comparing snapshots
 * - Undoing graph modifications
 * - Saving algorithm states
 */
class SnapshotDemo {
    public static void demonstrateGraphSnapshots() {
        System.out.println("=== Graph Snapshot Demo ===");
        System.out.println();

        Graph graph = Graph.create(5);
        SnapshotManager manager = new SnapshotManager(graph);

        // Initial snapshot
        graph.addEdge(0, 1, 4);
        manager.saveGraphSnapshot("Step 1: Added edge (0,1)");
        System.out.println("Snapshot 1: " + manager.getLatestGraphSnapshot());

        // Add more edges
        graph.addEdge(0, 2, 8);
        manager.saveGraphSnapshot("Step 2: Added edge (0,2)");
        System.out.println("Snapshot 2: " + manager.getLatestGraphSnapshot());

        graph.addEdge(1, 2, 3);
        manager.saveGraphSnapshot("Step 3: Added edge (1,2)");
        System.out.println("Snapshot 3: " + manager.getLatestGraphSnapshot());

        System.out.println();
        System.out.println("Graph snapshots saved: " + manager.getGraphSnapshotCount());
        System.out.println("Labels: " + manager.getGraphSnapshotLabels());
        System.out.println();

        // Undo and check
        System.out.println("Undoing last snapshot...");
        manager.undoGraphSnapshot();
        System.out.println("Latest snapshot: " + manager.getLatestGraphSnapshot());
        System.out.println();
    }

    public static void demonstrateSnapshotComparison() {
        System.out.println("=== Snapshot Comparison Demo ===");
        System.out.println();

        Graph graph = Graph.create(5);

        // Create initial snapshot
        graph.addEdge(0, 1, 4);
        GraphSnapshot snapshot1 = graph.createSnapshot();
        System.out.println("Snapshot 1: " + snapshot1);

        // Modify graph
        graph.addEdge(0, 2, 8);
        graph.addEdge(1, 2, 3);
        GraphSnapshot snapshot2 = graph.createSnapshot();
        System.out.println("Snapshot 2: " + snapshot2);

        // Compare
        System.out.println("Changes: " + snapshot2.describeChanges(snapshot1));
        System.out.println("Same state? " + snapshot1.isSameState(snapshot2));
        System.out.println();
    }

    public static void demonstrateGraphRestoration() {
        System.out.println("=== Graph Restoration Demo ===");
        System.out.println();

        Graph graph = Graph.create(5);
        graph.addEdge(0, 1, 4);
        graph.addEdge(0, 2, 8);
        graph.addEdge(1, 2, 3);

        // Capture state
        GraphSnapshot snapshot = graph.createSnapshot();
        System.out.println("Graph state captured: " + snapshot);
        System.out.println("Edges: " + snapshot.getEdgeCount());

        // Modify graph
        graph.addEdge(2, 3, 2);
        graph.addEdge(3, 4, 10);
        System.out.println("Graph modified - now has more edges");

        // Restore
        System.out.println("Restoring graph to snapshot...");
        graph.restoreFromSnapshot(snapshot);
        GraphSnapshot restored = graph.createSnapshot();
        System.out.println("Restored state: " + restored);
        System.out.println("Same as captured? " + snapshot.isSameState(restored));
        System.out.println();
    }

    public static void demonstrateAlgorithmSnapshot() {
        System.out.println("=== Algorithm Snapshot Demo ===");
        System.out.println();

        Graph graph = GraphBuilder.withVertexCount(5)
            .addEdge(0, 1, 4)
            .addEdge(0, 2, 8)
            .addEdge(1, 4, 6)
            .addEdge(1, 2, 3)
            .addEdge(2, 3, 2)
            .addEdge(3, 4, 10)
            .build();

        PathfindingAlgorithm dijkstra = new DijkstraShortestPathSolver();
        ShortestPathResult result = dijkstra.solve(graph, 0);

        // Create algorithm snapshot
        int[] distances = new int[result.getDistances().size()];
        for (int i = 0; i < distances.length; i++) {
            distances[i] = result.getDistances().get(i);
        }

        AlgorithmSnapshot snapshot = new AlgorithmSnapshot(
            "Dijkstra", 0, 5, distances, null, 5, true
        );

        System.out.println("Algorithm snapshot: " + snapshot);
        System.out.println("Description: " + snapshot.describe());
        System.out.println("Completed? " + snapshot.isCompleted());
        System.out.println();
    }

    public static void demonstrateSnapshotManager() {
        System.out.println("=== Snapshot Manager Demo ===");
        System.out.println();

        Graph graph = Graph.create(5);
        SnapshotManager manager = new SnapshotManager(graph);
        manager.setMaxSnapshots(10);

        System.out.println("Building graph with snapshot tracking...");
        graph.addEdge(0, 1, 4);
        manager.saveGraphSnapshot("Edge 1: (0,1)");

        graph.addEdge(0, 2, 8);
        manager.saveGraphSnapshot("Edge 2: (0,2)");

        graph.addEdge(1, 4, 6);
        manager.saveGraphSnapshot("Edge 3: (1,4)");

        graph.addEdge(1, 2, 3);
        manager.saveGraphSnapshot("Edge 4: (1,2)");

        graph.addEdge(2, 3, 2);
        manager.saveGraphSnapshot("Edge 5: (2,3)");

        System.out.println(manager.getHistorySummary());
        System.out.println("Labels: " + manager.getGraphSnapshotLabels());
        System.out.println();

        System.out.println("Changes from first to last snapshot:");
        System.out.println(manager.compareGraphSnapshots(0, 4));
        System.out.println();
    }

    public static void main(String[] args) {
        demonstrateGraphSnapshots();
        demonstrateSnapshotComparison();
        demonstrateGraphRestoration();
        demonstrateAlgorithmSnapshot();
        demonstrateSnapshotManager();
    }
}
