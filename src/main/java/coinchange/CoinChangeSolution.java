package coinchange;

public record CoinChangeSolution(CoinChangeProblem problem, int ways) {

    public CoinChangeSolution {
        if (problem == null) {
            throw new IllegalArgumentException("problem must not be null");
        }
        if (ways < 0) {
            throw new IllegalArgumentException("ways must not be negative");
        }
    }
}
