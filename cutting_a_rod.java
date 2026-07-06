class CuttingRod {

    public static void main(String[] args) {
        AppContext ctx = AppContext.withOutput(System.out);

        SolverType selected = (args.length > 0)
                ? SolverType.valueOf(args[0].toUpperCase())
                : null;

        int[] price = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        RodCuttingProblem arrayProblem = RodCuttingProblem.fromArray(price);
        RodCuttingProblem lambdaProblem = new RodCuttingProblem(8, length -> length * 3);

        for (SolverType type : SolverType.values()) {
            if (selected != null && type != selected) continue;
            RodCuttingSolver solver = ctx.solverFor(type);
            ctx.logger().logSolution(type, solver.solve(arrayProblem));
            ctx.logger().logSolution(type, solver.solve(lambdaProblem));
        }
    }
}
