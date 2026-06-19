package coinchange;

public final class CoinChangeDemo {

    private CoinChangeDemo() {
    }

    public static void main(String[] args) {
        int[] coins = {1, 2, 3};
        int targetSum = 5;
        System.out.println(CoinChange.count(coins, targetSum));
    }
}
