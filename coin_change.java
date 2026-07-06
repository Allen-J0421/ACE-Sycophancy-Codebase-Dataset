import java.util.Arrays;

class InvalidCoinChangeInputException extends RuntimeException {
    InvalidCoinChangeInputException(String message) {
        super(message);
    }
}

interface CoinChangeStrategy {
    int computeWays(int[] coins, int sum);
}

final class CoinChangeDPService implements CoinChangeStrategy {
    @Override
    public int computeWays(int[] coins, int sum) {
        if (coins == null) {
            throw new InvalidCoinChangeInputException("coins must not be null");
        }
        if (coins.length == 0) {
            throw new InvalidCoinChangeInputException("coins must not be empty");
        }
        if (sum < 0) {
            throw new InvalidCoinChangeInputException("sum must not be negative");
        }
        for (int coin : coins) {
            if (coin <= 0) {
                throw new InvalidCoinChangeInputException("each coin must be positive, got: " + coin);
            }
        }

        int[] dp = new int[sum + 1];

        dp[0] = 1;
        for (int coin : coins) {
            for (int j = coin; j <= sum; j++) {
                dp[j] += dp[j - coin];
            }
        }
        return dp[sum];
    }
}

public class CoinChange{

    private final CoinChangeStrategy strategy;

    public CoinChange(CoinChangeStrategy strategy) {
        this.strategy = strategy;
    }

    int count(int[] coins, int sum) {
        return strategy.computeWays(coins, sum);
    }

    public static void main(String[] args) {
        int[] coins = {1, 2, 3};
        int sum = 5;
        CoinChange solver = new CoinChange(new CoinChangeDPService());
        System.out.println(solver.count(coins, sum));
    }
}
