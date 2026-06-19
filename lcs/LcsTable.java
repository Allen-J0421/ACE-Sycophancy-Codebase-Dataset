package lcs;

final class LcsTable {
    private final CharSequence first;
    private final CharSequence second;
    private final int[][] lengths;

    private LcsTable(CharSequence first, CharSequence second, int[][] lengths) {
        this.first = first;
        this.second = second;
        this.lengths = lengths;
    }

    static LcsTable build(CharSequence first, CharSequence second) {
        int[][] lengths = new int[first.length() + 1][second.length() + 1];

        for (int i = 1; i <= first.length(); i++) {
            char firstChar = first.charAt(i - 1);
            for (int j = 1; j <= second.length(); j++) {
                if (firstChar == second.charAt(j - 1)) {
                    lengths[i][j] = lengths[i - 1][j - 1] + 1;
                } else {
                    lengths[i][j] = Math.max(lengths[i - 1][j], lengths[i][j - 1]);
                }
            }
        }

        return new LcsTable(first, second, lengths);
    }

    LcsResult toResult() {
        return new LcsResult(length(), subsequence());
    }

    int length() {
        return lengths[first.length()][second.length()];
    }

    String subsequence() {
        StringBuilder subsequence = new StringBuilder(length());
        int i = first.length();
        int j = second.length();
        while (i > 0 && j > 0) {
            if (first.charAt(i - 1) == second.charAt(j - 1)) {
                subsequence.append(first.charAt(i - 1));
                i--;
                j--;
            } else if (lengths[i - 1][j] >= lengths[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        return subsequence.reverse().toString();
    }
}
