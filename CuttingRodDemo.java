class CuttingRodDemo {
    public static void main(String[] args) {
        RodCuttingProblem problem = new RodCuttingProblem(
                new PriceTable(new int[]{0, 1, 5, 8, 9, 10, 17, 17, 20}));
        Solver solver = new RodCuttingSolver();
        RodCuttingSolution solution = solver.solve(problem);
        System.out.println(solution);
    }
}
