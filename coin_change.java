import java.util.Arrays;

interface CoinChangeStrategy {
    int computeWays(int[] coins, int sum);
}

final class CoinChangeDPService implements CoinChangeStrategy {
    @Override
    public int computeWays(int[] coins, int sum) {
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

    private static final CoinChangeStrategy STRATEGY = new CoinChangeDPService();

    static int count(int[] coins, int sum) {
        return STRATEGY.computeWays(coins, sum);
    }

    public static void main(String[] args) {
        int[] coins = {1, 2, 3};
        int sum = 5;
        System.out.println(count(coins, sum));
    }
}
