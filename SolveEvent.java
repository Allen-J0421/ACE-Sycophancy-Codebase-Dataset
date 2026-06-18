public class SolveEvent {
  public enum Type { BEFORE_SOLVE, AFTER_SOLVE, ERROR }

  private final Type type;
  private final int[] coins;
  private final int targetSum;
  private final CoinChangeResult result;
  private final Exception error;
  private final long timestamp;

  public SolveEvent(Type type, int[] coins, int targetSum) {
    this(type, coins, targetSum, null, null);
  }

  public SolveEvent(Type type, int[] coins, int targetSum, CoinChangeResult result) {
    this(type, coins, targetSum, result, null);
  }

  public SolveEvent(Type type, int[] coins, int targetSum, Exception error) {
    this(type, coins, targetSum, null, error);
  }

  private SolveEvent(Type type, int[] coins, int targetSum,
      CoinChangeResult result, Exception error) {
    this.type = type;
    this.coins = coins != null ? coins.clone() : new int[0];
    this.targetSum = targetSum;
    this.result = result;
    this.error = error;
    this.timestamp = System.currentTimeMillis();
  }

  public Type getType() { return type; }
  public int[] getCoins() { return coins.clone(); }
  public int getTargetSum() { return targetSum; }
  public CoinChangeResult getResult() { return result; }
  public Exception getError() { return error; }
  public long getTimestamp() { return timestamp; }
}
