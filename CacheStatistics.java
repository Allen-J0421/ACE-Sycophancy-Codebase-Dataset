public class CacheStatistics {
  private long hits = 0;
  private long misses = 0;
  private long evictions = 0;

  public void recordHit() {
    hits++;
  }

  public void recordMiss() {
    misses++;
  }

  public void recordEviction() {
    evictions++;
  }

  public long getHits() {
    return hits;
  }

  public long getMisses() {
    return misses;
  }

  public long getEvictions() {
    return evictions;
  }

  public long getTotalRequests() {
    return hits + misses;
  }

  public double getHitRate() {
    long total = getTotalRequests();
    return total > 0 ? (double) hits / total : 0;
  }

  public void reset() {
    hits = 0;
    misses = 0;
    evictions = 0;
  }

  @Override
  public String toString() {
    return String.format(
        "CacheStats{hits=%d, misses=%d, evictions=%d, hitRate=%.2f%%}",
        hits, misses, evictions, getHitRate() * 100);
  }
}
