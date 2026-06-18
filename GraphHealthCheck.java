public class GraphHealthCheck {
    private final Graph graph;
    private boolean valid = true;
    private java.util.List<String> issues = new java.util.ArrayList<>();

    public GraphHealthCheck(Graph graph) {
        this.graph = graph;
    }

    public HealthReport check() {
        issues.clear();
        valid = true;

        checkGraphStructure();
        checkVertexIntegrity();
        checkEdgeConsistency();

        return new HealthReport(valid, issues);
    }

    private void checkGraphStructure() {
        if (graph.getVertexCount() < 0) {
            addIssue("Negative vertex count: " + graph.getVertexCount());
        }
        if (graph.getVertexCount() == 0) {
            Logger.warn("Empty graph detected");
        }
    }

    private void checkVertexIntegrity() {
        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!graph.isValidVertex(i)) {
                addIssue("Vertex " + i + " marked invalid in valid range");
            }

            for (int neighbor : graph.getAdjacent(i)) {
                if (!graph.isValidVertex(neighbor)) {
                    addIssue("Vertex " + i + " references invalid neighbor " + neighbor);
                }
            }
        }
    }

    private void checkEdgeConsistency() {
        for (int i = 0; i < graph.getVertexCount(); i++) {
            for (int neighbor : graph.getAdjacent(i)) {
                boolean found = false;
                for (int other : graph.getAdjacent(neighbor)) {
                    if (other == i) {
                        found = true;
                        break;
                    }
                }
                if (!found && graph instanceof UndirectedGraph) {
                    addIssue("Undirected graph edge inconsistency: " + i + " -> " + neighbor);
                }
            }
        }
    }

    private void addIssue(String issue) {
        valid = false;
        issues.add(issue);
    }

    public static class HealthReport {
        private final boolean healthy;
        private final java.util.List<String> issues;

        public HealthReport(boolean healthy, java.util.List<String> issues) {
            this.healthy = healthy;
            this.issues = new java.util.ArrayList<>(issues);
        }

        public boolean isHealthy() {
            return healthy;
        }

        public java.util.List<String> getIssues() {
            return java.util.Collections.unmodifiableList(issues);
        }

        public void print() {
            System.out.println("=== Graph Health Report ===");
            System.out.println("Status: " + (healthy ? "HEALTHY ✓" : "ISSUES DETECTED ✗"));
            if (!issues.isEmpty()) {
                System.out.println("Issues:");
                for (String issue : issues) {
                    System.out.println("  - " + issue);
                }
            }
        }
    }
}
