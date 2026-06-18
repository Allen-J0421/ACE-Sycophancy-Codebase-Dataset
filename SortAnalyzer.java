import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SortAnalyzer {
    private final List<SortResult> results;

    public SortAnalyzer() {
        this.results = new ArrayList<>();
    }

    public void record(SortResult result) {
        results.add(result);
    }

    public void recordMultiple(List<SortResult> resultsToAdd) {
        results.addAll(resultsToAdd);
    }

    public double averageDurationMs() {
        return results.stream()
                .mapToDouble(SortResult::durationMs)
                .average()
                .orElse(0.0);
    }

    public double averageComparisons() {
        return results.stream()
                .mapToLong(SortResult::comparisons)
                .average()
                .orElse(0.0);
    }

    public double averageSwaps() {
        return results.stream()
                .mapToLong(SortResult::swaps)
                .average()
                .orElse(0.0);
    }

    public SortResult fastest() {
        return results.stream()
                .min((a, b) -> Long.compare(a.durationNanos(), b.durationNanos()))
                .orElse(null);
    }

    public SortResult slowest() {
        return results.stream()
                .max((a, b) -> Long.compare(a.durationNanos(), b.durationNanos()))
                .orElse(null);
    }

    public List<SortResult> successfulRuns() {
        return results.stream()
                .filter(SortResult::successful)
                .collect(Collectors.toList());
    }

    public long totalRuns() {
        return results.size();
    }

    public String summary() {
        return String.format(
                "Analysis Summary:\n" +
                "  Total Runs: %d\n" +
                "  Avg Duration: %.3f ms\n" +
                "  Avg Comparisons: %.0f\n" +
                "  Avg Swaps: %.0f\n" +
                "  Fastest: %.3f ms\n" +
                "  Slowest: %.3f ms",
                totalRuns(),
                averageDurationMs(),
                averageComparisons(),
                averageSwaps(),
                fastest() != null ? fastest().durationMs() : 0,
                slowest() != null ? slowest().durationMs() : 0
        );
    }
}
