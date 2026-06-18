import java.util.ArrayList;
import java.util.List;

public class CoinChangeSolver {
  private final CoinChangeStrategy strategy;
  private final boolean enableMetrics;
  private final List<SolveEventListener> listeners;

  public CoinChangeSolver(CoinChangeStrategy strategy) {
    this(strategy, false);
  }

  public CoinChangeSolver(CoinChangeStrategy strategy, boolean enableMetrics) {
    if (strategy == null) {
      throw new IllegalArgumentException("Strategy cannot be null");
    }
    this.strategy = strategy;
    this.enableMetrics = enableMetrics;
    this.listeners = new java.util.ArrayList<>();
  }

  public void addListener(SolveEventListener listener) {
    if (listener != null) {
      listeners.add(listener);
    }
  }

  public void removeListener(SolveEventListener listener) {
    listeners.remove(listener);
  }

  private void fireEvent(SolveEvent event) {
    for (SolveEventListener listener : listeners) {
      listener.onEvent(event);
    }
  }

  public CoinChangeResult solve(int[] coins, int targetSum) {
    return solve(coins, targetSum, false);
  }

  public CoinChangeResult solve(int[] coins, int targetSum, boolean isCached) {
    fireEvent(new SolveEvent(SolveEvent.Type.BEFORE_SOLVE, coins, targetSum));

    try {
      InputValidator.validate(coins, targetSum);

      if (targetSum == 0) {
        CoinChangeResult result = new CoinChangeResult(1, coins, targetSum);
        fireEvent(new SolveEvent(SolveEvent.Type.AFTER_SOLVE, coins, targetSum, result));
        return result;
      }

      long startTime = enableMetrics ? System.nanoTime() : 0;
      Runtime runtime = enableMetrics ? Runtime.getRuntime() : null;
      long memBefore = enableMetrics ? runtime.totalMemory() - runtime.freeMemory() : 0;

      int ways = strategy.countWays(coins, targetSum);

      SolveMetrics metrics = null;
      if (enableMetrics) {
        long endTime = System.nanoTime();
        long memAfter = runtime.totalMemory() - runtime.freeMemory();
        long executionTime = endTime - startTime;
        int memoryUsed = (int) (memAfter - memBefore);
        metrics = new SolveMetrics(executionTime, strategy.getClass().getSimpleName(), memoryUsed);
      }

      CoinChangeResult result = isCached ?
          new EnrichedCoinChangeResult(ways, coins, targetSum, metrics, true) :
          new CoinChangeResult(ways, coins, targetSum, metrics);

      fireEvent(new SolveEvent(SolveEvent.Type.AFTER_SOLVE, coins, targetSum, result));
      return result;

    } catch (Exception e) {
      fireEvent(new SolveEvent(SolveEvent.Type.ERROR, coins, targetSum, e));
      throw e;
    }
  }

  public BatchSolveResult solveBatch(BatchSolveRequest request) {
    long startTime = System.currentTimeMillis();
    List<CoinChangeResult> results = new ArrayList<>();

    for (BatchSolveRequest.SolveTask task : request.getTasks()) {
      try {
        CoinChangeResult result = solve(task.getCoins(), task.getTargetSum());
        results.add(result);
      } catch (Exception e) {
        // Continue with next task
      }
    }

    long endTime = System.currentTimeMillis();
    return new BatchSolveResult(results, endTime - startTime);
  }
}

