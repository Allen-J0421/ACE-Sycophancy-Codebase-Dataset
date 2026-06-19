/**
 * Reusable assertion helpers for LCS testing.
 * Consolidates common test patterns and improves test readability.
 */
class TestAssertions {

    /**
     * Asserts that LCS length is valid (non-negative and within bounds).
     * LCS length should always satisfy: 0 ≤ lcs_length ≤ min(len(s1), len(s2))
     *
     * @param lcsLength the computed LCS length
     * @param s1Length  length of first string
     * @param s2Length  length of second string
     */
    static void assertValidLcsLength(int lcsLength, int s1Length, int s2Length) {
        assert lcsLength >= 0 :
                "LCS length cannot be negative, got: " + lcsLength;

        int minLength = Math.min(s1Length, s2Length);
        assert lcsLength <= minLength :
                String.format("LCS length (%d) exceeds min string length (%d)",
                        lcsLength, minLength);
    }

    /**
     * Asserts that two LCS lengths are equal (for symmetry testing).
     * LCS(s1, s2) should always equal LCS(s2, s1).
     *
     * @param s1ToS2 LCS length of (s1, s2)
     * @param s2ToS1 LCS length of (s2, s1)
     * @param s1     first string (for error message)
     * @param s2     second string (for error message)
     */
    static void assertSymmetric(int s1ToS2, int s2ToS1, String s1, String s2) {
        assert s1ToS2 == s2ToS1 :
                String.format("Symmetry violated: LCS('%s','%s')=%d but LCS('%s','%s')=%d",
                        s1, s2, s1ToS2, s2, s1, s2ToS1);
    }

    /**
     * Asserts that an exception of expected type is thrown with expected message.
     *
     * @param action                  runnable that should throw exception
     * @param expectedExceptionType   type of exception expected
     * @param expectedMessageContent  substring that should appear in exception message
     */
    static void assertThrowsWithMessage(
            Runnable action,
            Class<? extends Exception> expectedExceptionType,
            String expectedMessageContent) {
        try {
            action.run();
            assert false :
                    "Expected " + expectedExceptionType.getSimpleName() + " to be thrown";
        } catch (Exception e) {
            assert expectedExceptionType.isInstance(e) :
                    "Wrong exception type. Expected: " + expectedExceptionType.getName() +
                            ", Got: " + e.getClass().getName();

            assert e.getMessage() != null && e.getMessage().contains(expectedMessageContent) :
                    "Exception message doesn't contain '" + expectedMessageContent + "'. " +
                            "Got: '" + e.getMessage() + "'";
        }
    }

    /**
     * Asserts that two solvers produce identical results.
     * Useful for comparing different implementations or variants.
     *
     * @param solver1 first solver
     * @param solver2 second solver
     * @param input   input to both solvers
     */
    static void assertSolversMatch(LcsSolver solver1, LcsSolver solver2, LcsInput input) {
        int result1 = solver1.solve(input).getLength();
        int result2 = solver2.solve(input).getLength();

        assert result1 == result2 :
                String.format("Solvers diverged: %s returned %d, %s returned %d",
                        solver1.getClass().getSimpleName(), result1,
                        solver2.getClass().getSimpleName(), result2);
    }

    /**
     * Asserts that three solvers all produce the same result.
     * Useful for validating standard, optimized, and cached variants.
     *
     * @param solver1 first solver
     * @param solver2 second solver
     * @param solver3 third solver
     * @param input   input to all solvers
     */
    static void assertAllSolversMatch(LcsSolver solver1, LcsSolver solver2,
                                       LcsSolver solver3, LcsInput input) {
        int result1 = solver1.solve(input).getLength();
        int result2 = solver2.solve(input).getLength();
        int result3 = solver3.solve(input).getLength();

        assert result1 == result2 && result2 == result3 :
                String.format("Solvers diverged: %d, %d, %d",
                        result1, result2, result3);
    }

    /**
     * Asserts that LCS computation matches its reconstruction.
     * The length of the reconstructed LCS should equal the computed length.
     *
     * @param computedLength the LCS length from solver
     * @param reconstructed  the reconstructed LCS string
     */
    static void assertReconstructionMatches(int computedLength, String reconstructed) {
        assert reconstructed.length() == computedLength :
                String.format("Length mismatch: computed=%d, reconstructed length=%d",
                        computedLength, reconstructed.length());
    }

    /**
     * Asserts that a string is a subsequence of another.
     * Used to verify that reconstructed LCS is actually a subsequence of both inputs.
     *
     * @param subsequence the supposed subsequence
     * @param string      the string to check against
     */
    static void assertIsSubsequence(String subsequence, String string) {
        int subIdx = 0;
        for (int i = 0; i < string.length() && subIdx < subsequence.length(); i++) {
            if (string.charAt(i) == subsequence.charAt(subIdx)) {
                subIdx++;
            }
        }
        assert subIdx == subsequence.length() :
                "'" + subsequence + "' is not a subsequence of '" + string + "'";
    }

    /**
     * Asserts that two strings are common subsequences (appear in both with same order).
     *
     * @param lcs first string
     * @param s1  second string
     * @param s2  third string
     */
    static void assertCommonSubsequence(String lcs, String s1, String s2) {
        assertIsSubsequence(lcs, s1);
        assertIsSubsequence(lcs, s2);
    }
}
