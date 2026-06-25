import java.util.ArrayList;
import java.util.List;

class RodCuttingSolver implements Solver {

    @Override
    public RodCuttingSolution solve(PriceTable prices) {
        int n = prices.rodLength();
        RevenueTable revenue = new RevenueTable(n);
        CutChoiceTable cutChoice = new CutChoiceTable(n);

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                int candidate = prices.priceAt(j) + revenue.get(i - j);
                if (candidate > revenue.get(i)) {
                    revenue.set(i, candidate);
                    cutChoice.set(i, j);
                }
            }
        }

        return new RodCuttingSolution(revenue.get(n), cutChoice.reconstructCuts(n));
    }

    private static class RevenueTable {
        private final int[] values;

        RevenueTable(int size) {
            values = new int[size + 1];
        }

        int get(int length) {
            return values[length];
        }

        void set(int length, int value) {
            values[length] = value;
        }
    }

    private static class CutChoiceTable {
        private final int[] values;

        CutChoiceTable(int size) {
            values = new int[size + 1];
        }

        void set(int length, int cut) {
            values[length] = cut;
        }

        List<Integer> reconstructCuts(int remaining) {
            List<Integer> cuts = new ArrayList<>();
            while (remaining > 0) {
                cuts.add(values[remaining]);
                remaining -= values[remaining];
            }
            return cuts;
        }
    }
}
