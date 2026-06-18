import java.util.ArrayList;
import java.util.List;

public class BatchSolveRequest {
  private final List<SolveTask> tasks;

  public BatchSolveRequest() {
    this.tasks = new ArrayList<>();
  }

  public void addTask(int[] coins, int targetSum) {
    tasks.add(new SolveTask(coins, targetSum));
  }

  public List<SolveTask> getTasks() {
    return new ArrayList<>(tasks);
  }

  public int size() {
    return tasks.size();
  }

  public static class SolveTask {
    private final int[] coins;
    private final int targetSum;

    public SolveTask(int[] coins, int targetSum) {
      this.coins = coins.clone();
      this.targetSum = targetSum;
    }

    public int[] getCoins() {
      return coins.clone();
    }

    public int getTargetSum() {
      return targetSum;
    }
  }
}
