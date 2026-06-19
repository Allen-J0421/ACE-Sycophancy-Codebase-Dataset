package coinchange;

import java.util.Arrays;
import java.util.List;

public final class CoinChangeSelfTest {
    private static final CoinChangeSolver SOLVER = new DynamicProgrammingCoinChangeSolver();
    private static final CoinChange SERVICE = CoinChanges.using(SOLVER);

    private CoinChangeSelfTest() {
    }

    public static void main(String[] args) {
        assertCount(new CountCase(new int[] {1, 2, 3}, 5, 5));
        assertCount(new CountCase(new int[] {2, 5, 3, 6}, 10, 5));
        assertCount(new CountCase(new int[] {3, 1, 2, 2}, 5, 5));
        assertCount(new CountCase(new int[] {4}, 3, 0));
        assertCount(new CountCase(new int[] {}, 0, 1));
        assertCount(new CountCase(new int[] {}, 4, 0));
        assertCount(new CoinDenominations(new int[] {1, 2, 3}), 5, 5);
        assertSolutionCarriesProblemContext();
        assertStandardUsesDynamicProgrammingSolver();
        assertRejectsNullSolver();
        assertDenominationsAreNormalized();
        assertDenominationsUseValueEquality();
        assertRejects(new int[] {1, 0, 3}, 4);
        assertRejects(new int[] {1, 2, 3}, -1);
        assertRejects((int[]) null, 4);
        assertRejects((CoinDenominations) null, 4);
        assertRejects(Arrays.asList(1, null, 3));
    }

    private static void assertCount(CountCase countCase) {
        int actualWays = CoinChanges.count(countCase.coins(), countCase.targetSum());
        if (actualWays != countCase.expectedWays()) {
            throw new AssertionError(
                "Expected " + countCase.expectedWays()
                    + " ways for target " + countCase.targetSum()
                    + " but got " + actualWays
            );
        }

        int serviceWays = SERVICE.countWays(countCase.coins(), countCase.targetSum());
        if (serviceWays != countCase.expectedWays()) {
            throw new AssertionError(
                "Expected service to return " + countCase.expectedWays()
                    + " ways for target " + countCase.targetSum()
                    + " but got " + serviceWays
            );
        }

        CoinChangeSolution solution = SERVICE.solve(countCase.coins(), countCase.targetSum());
        if (solution.ways() != countCase.expectedWays()) {
            throw new AssertionError(
                "Expected solution to contain " + countCase.expectedWays()
                    + " ways for target " + countCase.targetSum()
                    + " but got " + solution.ways()
            );
        }
    }

    private static void assertCount(CoinDenominations denominations, int targetSum, int expectedWays) {
        int actualWays = CoinChanges.count(denominations, targetSum);
        if (actualWays != expectedWays) {
            throw new AssertionError(
                "Expected " + expectedWays + " ways for target " + targetSum + " but got " + actualWays
            );
        }
    }

    private static void assertRejects(int[] coins, int targetSum) {
        try {
            CoinChanges.count(coins, targetSum);
            throw new AssertionError("Expected invalid input to be rejected");
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }

    private static void assertRejects(CoinDenominations denominations, int targetSum) {
        try {
            CoinChanges.count(denominations, targetSum);
            throw new AssertionError("Expected invalid input to be rejected");
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }

    private static void assertDenominationsAreNormalized() {
        CoinDenominations denominations = new CoinDenominations(new int[] {3, 1, 2, 2});
        if (!denominations.values().equals(List.of(1, 2, 3))) {
            throw new AssertionError("Expected denominations to be sorted and de-duplicated");
        }
    }

    private static void assertDenominationsUseValueEquality() {
        CoinDenominations left = new CoinDenominations(new int[] {1, 2, 3});
        CoinDenominations right = new CoinDenominations(List.of(1, 2, 3));
        if (!left.equals(right)) {
            throw new AssertionError("Expected denominations with same values to be equal");
        }
    }

    private static void assertStandardUsesDynamicProgrammingSolver() {
        if (!(CoinChanges.standard().solver() instanceof DynamicProgrammingCoinChangeSolver)) {
            throw new AssertionError("Expected standard CoinChange service to use the DP solver");
        }
    }

    private static void assertSolutionCarriesProblemContext() {
        CoinChangeProblem problem = new CoinChangeProblem(new int[] {1, 2, 3}, 5);
        CoinChangeSolution solution = CoinChanges.solve(problem);
        if (!solution.problem().equals(problem)) {
            throw new AssertionError("Expected solution to retain the originating problem");
        }
    }

    private static void assertRejectsNullSolver() {
        try {
            CoinChanges.using(null);
            throw new AssertionError("Expected null solver to be rejected");
        } catch (NullPointerException expected) {
            // Expected path.
        }
    }

    private static void assertRejects(List<Integer> values) {
        try {
            new CoinDenominations(values);
            throw new AssertionError("Expected invalid denominations to be rejected");
        } catch (IllegalArgumentException expected) {
            // Expected path.
        }
    }

    private record CountCase(int[] coins, int targetSum, int expectedWays) {
    }
}
