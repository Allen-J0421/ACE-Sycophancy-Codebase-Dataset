import java.util.Set;
import java.util.HashSet;

public class ResultValidator {
    private final TraversalResult result;
    private final Graph graph;

    public ResultValidator(TraversalResult result, Graph graph) {
        this.result = result;
        this.graph = graph;
    }

    public ValidationReport validate() {
        ValidationReport report = new ValidationReport();

        report.addCheck("All vertices covered", validateAllVerticesCovered());
        report.addCheck("No duplicate vertices", validateNoDuplicates());
        report.addCheck("No invalid vertices", validateNoInvalidVertices());
        report.addCheck("Component count correct", validateComponentCount());
        report.addCheck("Connectivity preserved", validateConnectivityPreserved());

        return report;
    }

    private boolean validateAllVerticesCovered() {
        Set<Integer> visited = new HashSet<>(result.getVertices());
        return visited.size() == graph.getVertexCount();
    }

    private boolean validateNoDuplicates() {
        Set<Integer> seen = new HashSet<>();
        for (int vertex : result.getVertices()) {
            if (!seen.add(vertex)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateNoInvalidVertices() {
        for (int vertex : result.getVertices()) {
            if (!graph.isValidVertex(vertex)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateComponentCount() {
        boolean[] visited = new boolean[graph.getVertexCount()];
        int components = 0;

        for (int i = 0; i < graph.getVertexCount(); i++) {
            if (!visited[i]) {
                dfs(i, visited);
                components++;
            }
        }

        return components == result.getComponentCount();
    }

    private boolean validateConnectivityPreserved() {
        for (int vertex : result.getVertices()) {
            for (int neighbor : graph.getAdjacent(vertex)) {
                if (!result.getVertices().contains(neighbor)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void dfs(int vertex, boolean[] visited) {
        visited[vertex] = true;
        for (int neighbor : graph.getAdjacent(vertex)) {
            if (!visited[neighbor]) {
                dfs(neighbor, visited);
            }
        }
    }

    public static class ValidationReport {
        private final java.util.List<Check> checks = new java.util.ArrayList<>();

        public void addCheck(String name, boolean passed) {
            checks.add(new Check(name, passed));
        }

        public boolean isValid() {
            return checks.stream().allMatch(c -> c.passed);
        }

        public void print() {
            System.out.println("=== Validation Report ===");
            for (Check check : checks) {
                System.out.println((check.passed ? "✓" : "✗") + " " + check.name);
            }
            System.out.println("Overall: " + (isValid() ? "VALID" : "INVALID"));
        }

        private static class Check {
            String name;
            boolean passed;

            Check(String name, boolean passed) {
                this.name = name;
                this.passed = passed;
            }
        }
    }
}
