import java.util.ArrayList;
import java.util.List;

public class EnrichedCoinChangeResult extends CoinChangeResult {
  private final List<String> metadata;
  private final boolean isCached;
  private final long createdAt;

  public EnrichedCoinChangeResult(int ways, int[] coins, int targetSum,
      SolveMetrics metrics, boolean isCached) {
    super(ways, coins, targetSum, metrics);
    this.metadata = new ArrayList<>();
    this.isCached = isCached;
    this.createdAt = System.currentTimeMillis();
  }

  public void addMetadata(String info) {
    metadata.add(info);
  }

  public List<String> getMetadata() {
    return new ArrayList<>(metadata);
  }

  public boolean isCached() {
    return isCached;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  @Override
  public String toString() {
    String base = super.toString();
    String cached = isCached ? " [CACHED]" : "";
    return base + cached;
  }
}
