import java.util.ArrayList;
import java.util.List;

class RodCuttingSolver {

    RodCuttingSolution solve(RodCuttingProblem problem) {
        int n = problem.rodLength();
        int[] revenue = new int[n + 1];
        int[] bestCut = new int[n + 1];

        for (int length = 1; length <= n; length++) {
            for (int firstCut = 1; firstCut <= length; firstCut++) {
                int candidate = problem.priceFor(firstCut) + revenue[length - firstCut];
                if (candidate > revenue[length]) {
                    revenue[length] = candidate;
                    bestCut[length] = firstCut;
                }
            }
        }

        List<Integer> cuts = new ArrayList<>();
        int remaining = n;
        while (remaining > 0) {
            cuts.add(bestCut[remaining]);
            remaining -= bestCut[remaining];
        }

        return new RodCuttingSolution(revenue[n], cuts);
    }
}
