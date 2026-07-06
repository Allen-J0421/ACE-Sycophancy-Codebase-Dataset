import java.util.Arrays;

class InvalidCoinChangeInputException extends RuntimeException {
    InvalidCoinChangeInputException(String message) {
        super(message);
    }
}

@FunctionalInterface
interface Validator {
    void validate(int[] coins, int sum);
}

final class CompositeValidator implements Validator {
    private final Validator[] validators;

    private CompositeValidator(Validator... validators) {
        this.validators = validators;
    }

    static CompositeValidator of(Validator... validators) {
        return new CompositeValidator(validators);
    }

    @Override
    public void validate(int[] coins, int sum) {
        for (Validator v : validators) v.validate(coins, sum);
    }
}

interface CoinChangeStrategy {
    int computeWays(int[] coins, int sum);
}

final class CoinChangeDPService implements CoinChangeStrategy {
    private static final Validator VALIDATOR = CompositeValidator.of(
        (coins, sum) -> { if (coins == null)     throw new InvalidCoinChangeInputException("coins must not be null"); },
        (coins, sum) -> { if (coins.length == 0) throw new InvalidCoinChangeInputException("coins must not be empty"); },
        (coins, sum) -> { if (sum < 0)           throw new InvalidCoinChangeInputException("sum must not be negative"); },
        (coins, sum) -> Arrays.stream(coins)
                              .filter(c -> c <= 0)
                              .findFirst()
                              .ifPresent(c -> { throw new InvalidCoinChangeInputException("each coin must be positive, got: " + c); })
    );

    @Override
    public int computeWays(int[] coins, int sum) {
        VALIDATOR.validate(coins, sum);

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
