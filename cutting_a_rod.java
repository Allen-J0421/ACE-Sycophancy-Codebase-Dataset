class CuttingRod {

    public static void main(String[] args) {
        int[] price = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        RodCuttingProblem problem = new RodCuttingProblem(price);

        SolverType selected = (args.length > 0)
                ? SolverType.valueOf(args[0].toUpperCase())
                : null;

        for (SolverType type : SolverType.values()) {
            if (selected != null && type != selected) continue;
            RodCuttingSolution solution = new RodCuttingSolver(RodCuttingStrategyFactory.create(type)).solve(problem);
            System.out.println(type + " - max revenue: " + solution.maxRevenue() + ", cuts: " + solution.cuts());
        }
    }
}
