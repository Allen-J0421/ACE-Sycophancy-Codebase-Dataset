/**
 * Encapsulates input data for LCS computation with validation.
 */
class LcsInput {
    private final String firstString;
    private final String secondString;

    /**
     * Constructs an LcsInput with validation.
     *
     * @param firstString  the first string (non-null)
     * @param secondString the second string (non-null)
     * @throws InvalidInputException if firstString is null
     * @throws InvalidInputException if secondString is null
     */
    public LcsInput(String firstString, String secondString) {
        if (firstString == null) {
            throw new InvalidInputException("First string cannot be null");
        }
        if (secondString == null) {
            throw new InvalidInputException("Second string cannot be null");
        }
        this.firstString = firstString;
        this.secondString = secondString;
    }

    public String getFirstString() {
        return firstString;
    }

    public String getSecondString() {
        return secondString;
    }
}

/**
 * Encapsulates the result of LCS computation.
 */
class LcsResult {
    private final int length;

    public LcsResult(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }
}

/**
 * Interface for LCS solver implementations.
 */
interface LcsSolver {
    /**
     * Computes the length of the Longest Common Subsequence.
     *
     * @param input the LcsInput containing two strings
     * @return LcsResult with the length of the LCS
     */
    LcsResult solve(LcsInput input);
}

/**
 * Standard dynamic programming implementation of LCS algorithm.
 */
class StandardLcsSolver implements LcsSolver {
    /**
     * Computes the length of the Longest Common Subsequence using DP.
     * Time complexity: O(m * n), Space complexity: O(m * n)
     *
     * @param input the LcsInput containing two strings
     * @return LcsResult with the length of the LCS
     */
    @Override
    public LcsResult solve(LcsInput input) {
        String s1 = input.getFirstString();
        String s2 = input.getSecondString();

        int m = s1.length();
        int n = s2.length();

        // Use shared DP table builder to eliminate duplication
        int[][] dpTable = DpTableBuilder.buildTable(s1, s2);

        return new LcsResult(dpTable[m][n]);
    }
}

/**
 * Backward-compatible wrapper maintaining original static API.
 */
class LongestCommonSubsequence {
    private static final LcsSolver solver = new StandardLcsSolver();

    /**
     * Computes the length of the Longest Common Subsequence.
     * Backward-compatible convenience method using standard DP solver.
     *
     * @param s1 first string
     * @param s2 second string
     * @return length of the LCS
     * @throws IllegalArgumentException if either string is null
     */
    static int lcs(String s1, String s2) {
        LcsInput input = new LcsInput(s1, s2);
        return solver.solve(input).getLength();
    }
}
