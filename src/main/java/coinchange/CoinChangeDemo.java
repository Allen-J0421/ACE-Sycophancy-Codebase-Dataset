package coinchange;

public final class CoinChangeDemo {

    private CoinChangeDemo() {
    }

    public static void main(String[] args) {
        CoinChangeProblem problem = new CoinChangeProblem(new int[] {1, 2, 3}, 5);
        System.out.println(CoinChanges.solve(problem).ways());
    }
}
