import java.util.ArrayList;
import java.util.List;

class RodCuttingSolver implements Solver {

    @Override
    public RodCuttingSolution solve(PriceTable prices) {
        int n = prices.rodLength();
        int[] revenue = new int[n + 1];
        int[] cutChoice = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                int candidate = prices.priceAt(j) + revenue[i - j];
                if (candidate > revenue[i]) {
                    revenue[i] = candidate;
                    cutChoice[i] = j;
                }
            }
        }

        return new RodCuttingSolution(revenue[n], reconstructCuts(cutChoice, n));
    }

    private List<Integer> reconstructCuts(int[] cutChoice, int remaining) {
        List<Integer> cuts = new ArrayList<>();
        while (remaining > 0) {
            cuts.add(cutChoice[remaining]);
            remaining -= cutChoice[remaining];
        }
        return cuts;
    }
}
