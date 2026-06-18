import java.util.ArrayList;
import java.util.List;

public class BatchSolveResult {
  private final List<CoinChangeResult> results;
  private final long totalTimeMillis;
  private final int successCount;

  public BatchSolveResult(List<CoinChangeResult> results, long totalTimeMillis) {
    this.results = new ArrayList<>(results);
    this.totalTimeMillis = totalTimeMillis;
    this.successCount = results.size();
  }

  public List<CoinChangeResult> getResults() {
    return new ArrayList<>(results);
  }

  public CoinChangeResult getResult(int index) {
    return results.get(index);
  }

  public int getSuccessCount() {
    return successCount;
  }

  public long getTotalTimeMillis() {
    return totalTimeMillis;
  }

  public double getAverageTimeMillis() {
    return successCount > 0 ? (double) totalTimeMillis / successCount : 0;
  }

  @Override
  public String toString() {
    return String.format("BatchSolveResult{solved=%d, totalTime=%dms, avgTime=%.2fms}",
        successCount, totalTimeMillis, getAverageTimeMillis());
  }
}
