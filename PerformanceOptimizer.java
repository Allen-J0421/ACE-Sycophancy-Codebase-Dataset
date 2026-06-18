import java.util.ArrayList;
import java.util.List;

public class PerformanceOptimizer {
    private final Graph graph;

    public PerformanceOptimizer(Graph graph) {
        this.graph = graph;
    }

    public OptimizationReport analyze() {
        OptimizationReport report = new OptimizationReport();

        // Analyze graph density
        int edgeCount = countEdges();
        int maxEdges = graph.getVertexCount() * (graph.getVertexCount() - 1) / 2;
        double density = maxEdges > 0 ? (double) edgeCount / maxEdges : 0;
        report.addMetric("density", density);

        // Analyze vertex distribution
        int maxDegree = 0, minDegree = Integer.MAX_VALUE;
        for (int i = 0; i < graph.getVertexCount(); i++) {
            int degree = graph.getAdjacent(i).size();
            maxDegree = Math.max(maxDegree, degree);
            minDegree = Math.min(minDegree, degree);
        }
        report.addMetric("maxDegree", maxDegree);
        report.addMetric("minDegree", minDegree);

        // Recommend optimizations
        if (density < 0.1) {
            report.addRecommendation("Graph is sparse - consider adjacency list representation");
        }
        if (maxDegree > graph.getVertexCount() / 2) {
            report.addRecommendation("High-degree vertices detected - consider indexed lookups");
        }

        return report;
    }

    private int countEdges() {
        int count = 0;
        for (int i = 0; i < graph.getVertexCount(); i++) {
            count += graph.getAdjacent(i).size();
        }
        return count / 2;
    }

    public static class OptimizationReport {
        private final java.util.Map<String, Double> metrics = new java.util.HashMap<>();
        private final List<String> recommendations = new ArrayList<>();

        public void addMetric(String name, double value) {
            metrics.put(name, value);
        }

        public void addRecommendation(String recommendation) {
            recommendations.add(recommendation);
        }

        public java.util.Map<String, Double> getMetrics() {
            return new java.util.HashMap<>(metrics);
        }

        public List<String> getRecommendations() {
            return new ArrayList<>(recommendations);
        }

        public void print() {
            System.out.println("\n=== Performance Optimization Report ===");
            System.out.println("Metrics:");
            for (java.util.Map.Entry<String, Double> entry : metrics.entrySet()) {
                System.out.printf("  %s: %.4f%n", entry.getKey(), entry.getValue());
            }
            System.out.println("Recommendations:");
            for (String rec : recommendations) {
                System.out.println("  - " + rec);
            }
        }
    }
}
