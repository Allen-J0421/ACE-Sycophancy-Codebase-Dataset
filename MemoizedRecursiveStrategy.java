import java.util.Arrays;

class MemoizedRecursiveStrategy implements RodCuttingStrategy {

    @Override
    public RodCuttingSolution solve(RodCuttingProblem problem) {
        int n = problem.rodLength();
        int[] memo = new int[n + 1];
        int[] bestCut = new int[n + 1];
        Arrays.fill(memo, -1);
        memo[0] = 0;

        computeRevenue(n, problem, memo, bestCut);

        return new RodCuttingSolution(memo[n], CutTracer.trace(bestCut, n));
    }

    private int computeRevenue(int length, RodCuttingProblem problem, int[] memo, int[] bestCut) {
        if (memo[length] >= 0) return memo[length];
        int best = 0;
        for (int cut = 1; cut <= length; cut++) {
            int candidate = problem.priceFor(cut) + computeRevenue(length - cut, problem, memo, bestCut);
            if (candidate > best) {
                best = candidate;
                bestCut[length] = cut;
            }
        }
        memo[length] = best;
        return best;
    }
}
