class CuttingRod {

    public static void main(String[] args) {
        int[] price = {0, 1, 5, 8, 9, 10, 17, 17, 20};
        RodCuttingProblem problem = new RodCuttingProblem(price);
        RodCuttingSolution solution = new RodCuttingSolver().solve(problem);
        System.out.println("Max revenue: " + solution.maxRevenue());
        System.out.println("Optimal cuts: " + solution.cuts());
    }
}
