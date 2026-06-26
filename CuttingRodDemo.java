class CuttingRodDemo {
    public static void main(String[] args) {
        PriceTable prices = new PriceTable(new int[]{0, 1, 5, 8, 9, 10, 17, 17, 20});
        Solver solver = new RodCuttingSolver();
        RodCuttingSolution solution = solver.solve(prices);
        System.out.println(solution);
    }
}
