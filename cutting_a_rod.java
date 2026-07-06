class CuttingRod {

    public static void main(String[] args) {
        int[] price = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        RodCuttingProblem problem = new RodCuttingProblem(price);

        RodCuttingSolution iterative = new RodCuttingSolver(new IterativeDPStrategy()).solve(problem);
        System.out.println("Iterative DP    - max revenue: " + iterative.maxRevenue() + ", cuts: " + iterative.cuts());

        RodCuttingSolution memoized = new RodCuttingSolver(new MemoizedRecursiveStrategy()).solve(problem);
        System.out.println("Memoized Recur. - max revenue: " + memoized.maxRevenue() + ", cuts: " + memoized.cuts());
    }
}
