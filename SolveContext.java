public class SolveContext {
  private final String requestId;
  private final int[] coins;
  private final int targetSum;
  private final long createdAt;
  private final java.util.Map<String, Object> metadata;

  public SolveContext(String requestId, int[] coins, int targetSum) {
    this.requestId = requestId;
    this.coins = coins.clone();
    this.targetSum = targetSum;
    this.createdAt = System.currentTimeMillis();
    this.metadata = new java.util.HashMap<>();
  }

  public String getRequestId() {
    return requestId;
  }

  public int[] getCoins() {
    return coins.clone();
  }

  public int getTargetSum() {
    return targetSum;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public void setAttribute(String key, Object value) {
    metadata.put(key, value);
  }

  public Object getAttribute(String key) {
    return metadata.get(key);
  }

  public java.util.Map<String, Object> getMetadata() {
    return new java.util.HashMap<>(metadata);
  }

  @Override
  public String toString() {
    return String.format("SolveContext{requestId=%s, sum=%d, coins=%d}",
        requestId, targetSum, coins.length);
  }
}
