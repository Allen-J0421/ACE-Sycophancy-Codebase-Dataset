import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

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
        List.of(
            Map.entry((BooleanSupplier) () -> coins == null,     "coins must not be null"),
            Map.entry((BooleanSupplier) () -> coins.length == 0, "coins must not be empty"),
            Map.entry((BooleanSupplier) () -> sum < 0,           "sum must not be negative")
        ).stream()
         .filter(rule -> rule.getKey().getAsBoolean())
         .findFirst()
         .ifPresent(rule -> { throw new InvalidCoinChangeInputException(rule.getValue()); });

        Arrays.stream(coins)
              .filter(c -> c <= 0)
              .findFirst()
              .ifPresent(c -> { throw new InvalidCoinChangeInputException("each coin must be positive, got: " + c); });

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

    private CoinChange(Builder builder) {
        this.strategy = builder.strategy;
    }

    int count(int[] coins, int sum) {
        return strategy.computeWays(coins, sum);
    }

    static class Builder {
        private CoinChangeStrategy strategy = new CoinChangeDPService();

        Builder strategy(CoinChangeStrategy strategy) {
            if (strategy == null) {
                throw new IllegalArgumentException("strategy must not be null");
            }
            this.strategy = strategy;
            return this;
        }

        CoinChange build() {
            return new CoinChange(this);
        }
    }

    public static void main(String[] args) {
        int[] coins = {1, 2, 3};
        int sum = 5;
        CoinChange solver = new CoinChange.Builder()
            .strategy(new CoinChangeDPService())
            .build();
        System.out.println(solver.count(coins, sum));
    }
}
