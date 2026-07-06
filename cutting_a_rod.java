class CuttingRod {

    public static void main(String[] args) {
        SolverType selected = (args.length > 0)
                ? SolverType.valueOf(args[0].toUpperCase())
                : null;

        int[] price = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        RodCuttingProblem arrayProblem = RodCuttingProblem.fromArray(price);

        // price grows linearly: selling whole rod of length n yields n*3
        RodCuttingProblem lambdaProblem = new RodCuttingProblem(8, length -> length * 3);

        SolverResultPrinter printer = new SolverResultPrinter(System.out);
        for (SolverType type : SolverType.values()) {
            if (selected != null && type != selected) continue;
            RodCuttingSolver solver = new RodCuttingSolver(RodCuttingStrategyFactory.create(type, System.out));
            printer.print(type, solver.solve(arrayProblem));
            printer.print(type, solver.solve(lambdaProblem));
        }
    }
}
