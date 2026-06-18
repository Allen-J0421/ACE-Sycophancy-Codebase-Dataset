package sorting.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MetricsCollector {
    private final List<SortMetrics> metrics;

    public MetricsCollector() {
        this.metrics = Collections.synchronizedList(new ArrayList<>());
    }

    public void record(SortMetrics metric) {
        metrics.add(metric);
    }

    public List<SortMetrics> getMetrics() {
        return new ArrayList<>(metrics);
    }

    public double averageDurationMs() {
        return metrics.stream()
                .mapToDouble(SortMetrics::durationMs)
                .average()
                .orElse(0.0);
    }

    public double averageComparisons() {
        return metrics.stream()
                .mapToLong(SortMetrics::comparisons)
                .average()
                .orElse(0.0);
    }

    public double medianDurationMs() {
        List<SortMetrics> sorted = metrics.stream()
                .sorted(Comparator.comparingLong(SortMetrics::durationNanos))
                .collect(Collectors.toList());

        if (sorted.isEmpty()) return 0.0;
        int mid = sorted.size() / 2;
        return sorted.size() % 2 == 0
                ? (sorted.get(mid - 1).durationMs() + sorted.get(mid).durationMs()) / 2
                : sorted.get(mid).durationMs();
    }

    public SortMetrics fastest() {
        return metrics.stream()
                .min(Comparator.comparingLong(SortMetrics::durationNanos))
                .orElse(null);
    }

    public SortMetrics slowest() {
        return metrics.stream()
                .max(Comparator.comparingLong(SortMetrics::durationNanos))
                .orElse(null);
    }

    public double standardDeviation() {
        if (metrics.isEmpty()) return 0.0;
        double mean = averageDurationMs();
        double variance = metrics.stream()
                .mapToDouble(m -> Math.pow(m.durationMs() - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }

    public String generateReport() {
        return String.format(
                """
                Metrics Report:
                  Total Runs: %d
                  Average Duration: %.3f ms
                  Median Duration: %.3f ms
                  Std Dev: %.3f ms
                  Fastest: %.3f ms
                  Slowest: %.3f ms
                  Avg Comparisons: %.0f
                  Avg Swaps: %.0f""",
                metrics.size(),
                averageDurationMs(),
                medianDurationMs(),
                standardDeviation(),
                fastest() != null ? fastest().durationMs() : 0,
                slowest() != null ? slowest().durationMs() : 0,
                averageComparisons(),
                metrics.stream().mapToLong(SortMetrics::swaps).average().orElse(0.0)
        );
    }

    public void clear() {
        metrics.clear();
    }
}
