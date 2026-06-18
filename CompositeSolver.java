import java.util.ArrayList;
import java.util.List;

public class CompositeSolver {
  private final List<CoinChangeStrategy> solvers;
  private final String name;

  public CompositeSolver(String name) {
    this.name = name;
    this.solvers = new ArrayList<>();
  }

  public void addSolver(CoinChangeStrategy solver) {
    solvers.add(solver);
  }

  public List<Integer> solveWithAll(int[] coins, int targetSum) {
    List<Integer> results = new ArrayList<>();
    for (CoinChangeStrategy solver : solvers) {
      int result = solver.countWays(coins, targetSum);
      results.add(result);
    }
    return results;
  }

  public int solveWithVoting(int[] coins, int targetSum) {
    List<Integer> results = solveWithAll(coins, targetSum);
    if (results.isEmpty()) {
      return 0;
    }

    // Return most common result (majority voting)
    java.util.Map<Integer, Integer> votes = new java.util.HashMap<>();
    for (int result : results) {
      votes.put(result, votes.getOrDefault(result, 0) + 1);
    }

    return votes.entrySet().stream()
        .max((a, b) -> Integer.compare(a.getValue(), b.getValue()))
        .map(e -> e.getKey())
        .orElse(0);
  }

  public int getSolverCount() {
    return solvers.size();
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return String.format("CompositeSolver{name=%s, solvers=%d}", name, solvers.size());
  }
}
