import java.util.Arrays;
import java.util.Objects;

public class CacheKey {
  private final int[] coins;
  private final int targetSum;

  public CacheKey(int[] coins, int targetSum) {
    this.coins = coins.clone();
    this.targetSum = targetSum;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CacheKey cacheKey = (CacheKey) o;
    return targetSum == cacheKey.targetSum && Arrays.equals(coins, cacheKey.coins);
  }

  @Override
  public int hashCode() {
    return Objects.hash(Arrays.hashCode(coins), targetSum);
  }
}
