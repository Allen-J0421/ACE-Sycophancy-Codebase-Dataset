import java.util.ArrayList;
import java.util.List;

public class ResultAggregator {
  private final List<CoinChangeResult> results;
  private final long createdAt;

  public ResultAggregator() {
    this.results = new ArrayList<>();
    this.createdAt = System.currentTimeMillis();
  }

  public void add(CoinChangeResult result) {
    if (result != null) {
      results.add(result);
    }
  }

  public void addAll(List<CoinChangeResult> resultList) {
    results.addAll(resultList);
  }

  public List<CoinChangeResult> getResults() {
    return new ArrayList<>(results);
  }

  public int getTotalResults() {
    return results.size();
  }

  public long getTotalWays() {
    return results.stream().mapToLong(CoinChangeResult::getWays).sum();
  }

  public double getAverageWays() {
    return results.isEmpty() ? 0 : (double) getTotalWays() / results.size();
  }

  public long getMaxWays() {
    return results.stream().mapToLong(CoinChangeResult::getWays).max().orElse(0);
  }

  public long getMinWays() {
    return results.stream().mapToLong(CoinChangeResult::getWays).min().orElse(0);
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public String generateSummary() {
    return String.format(
        "=== Result Aggregation Summary ===\n" +
        "  Total Results: %d\n" +
        "  Total Ways: %d\n" +
        "  Average Ways: %.2f\n" +
        "  Max Ways: %d\n" +
        "  Min Ways: %d",
        getTotalResults(), getTotalWays(), getAverageWays(),
        getMaxWays(), getMinWays()
    );
  }

  @Override
  public String toString() {
    return String.format("ResultAggregator{results=%d, totalWays=%d, avg=%.2f}",
        getTotalResults(), getTotalWays(), getAverageWays());
  }
}
