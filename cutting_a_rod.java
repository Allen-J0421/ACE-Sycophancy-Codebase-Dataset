import java.util.ArrayList;
import java.util.List;

class CuttingRod {

    static int maxRevenue(int[] price) {
        int n = price.length - 1;
        int[] revenue = new int[n + 1];

        for (int length = 1; length <= n; length++) {
            for (int firstCut = 1; firstCut <= length; firstCut++) {
                revenue[length] = Math.max(revenue[length], price[firstCut] + revenue[length - firstCut]);
            }
        }

        return revenue[n];
    }

    static List<Integer> findOptimalCuts(int[] price) {
        int n = price.length - 1;
        int[] revenue = new int[n + 1];
        int[] bestCut = new int[n + 1];

        for (int length = 1; length <= n; length++) {
            for (int firstCut = 1; firstCut <= length; firstCut++) {
                int candidate = price[firstCut] + revenue[length - firstCut];
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
        return cuts;
    }

    public static void main(String[] args) {
        int[] price = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        System.out.println("Max revenue: " + maxRevenue(price));
        System.out.println("Optimal cuts: " + findOptimalCuts(price));
    }
}
