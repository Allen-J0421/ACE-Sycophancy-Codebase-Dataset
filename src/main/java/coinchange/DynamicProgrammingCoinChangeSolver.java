package coinchange;

public final class DynamicProgrammingCoinChangeSolver implements CoinChangeSolver {

    @Override
    public CoinChangeSolution solve(CoinChangeProblem problem) {
        int targetSum = problem.targetSum();
        int[] waysBySum = new int[targetSum + 1];
        waysBySum[0] = 1;

        for (int coin : problem.denominations().values()) {
            for (int currentSum = coin; currentSum <= targetSum; currentSum++) {
                waysBySum[currentSum] += waysBySum[currentSum - coin];
            }
        }

        return new CoinChangeSolution(problem, waysBySum[targetSum]);
    }
}
