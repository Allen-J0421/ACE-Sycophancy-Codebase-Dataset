import java.util.Objects;

public final class LongestCommonSubsequence {
    private LongestCommonSubsequence() {
        // Utility class.
    }

    public static int lcs(String first, String second) {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");

        if (first.isEmpty() || second.isEmpty()) {
            return 0;
        }

        if (second.length() > first.length()) {
            return lcs(second, first);
        }

        int[] previous = new int[second.length() + 1];
        int[] current = new int[second.length() + 1];

        for (int i = 1; i <= first.length(); i++) {
            char firstChar = first.charAt(i - 1);
            for (int j = 1; j <= second.length(); j++) {
                if (firstChar == second.charAt(j - 1)) {
                    current[j] = previous[j - 1] + 1;
                } else {
                    current[j] = Math.max(previous[j], current[j - 1]);
                }
            }

            int[] swap = previous;
            previous = current;
            current = swap;
        }

        return previous[second.length()];
    }

    public static void main(String[] args) {
        String first = "AGGTAB";
        String second = "GXTXAYB";
        System.out.println(lcs(first, second));
    }
}
