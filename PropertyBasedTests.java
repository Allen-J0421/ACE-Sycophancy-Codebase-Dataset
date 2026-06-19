/**
 * Property-based tests for LCS algorithm correctness.
 * Tests invariants that should hold across all valid inputs.
 */
class PropertyBasedTests {

    public static void main(String[] args) {
        testSymmetryProperty();
        testMonotonicityProperty();
        testSubsequenceProperty();
        testIdempotencyProperty();
        testCompositionProperty();
        testScalingProperty();
        System.out.println("\n✓ All property-based tests passed");
    }

    /**
     * Property 1: Symmetry - LCS(s1, s2) == LCS(s2, s1)
     */
    static void testSymmetryProperty() {
        System.out.println("=== Symmetry Property ===\n");
        LcsSolver solver = new StandardLcsSolver();

        String[][] testPairs = {
                {"ABC", "DEF"},
                {"AGGTAB", "GXTXAYB"},
                {"HELLO", "WORLD"},
                {"", "ABC"},
                {"XYZ", "XYZ"}
        };

        for (String[] pair : testPairs) {
            int forward = solver.solve(new LcsInput(pair[0], pair[1])).getLength();
            int backward = solver.solve(new LcsInput(pair[1], pair[0])).getLength();
            assert forward == backward : String.format("Symmetry violated: %d != %d", forward, backward);
            System.out.println("✓ LCS(" + pair[0] + ", " + pair[1] + ") = " + forward);
        }
        System.out.println();
    }

    /**
     * Property 2: Monotonicity - LCS(s1, s2) <= min(|s1|, |s2|)
     */
    static void testMonotonicityProperty() {
        System.out.println("=== Monotonicity Property ===\n");
        LcsSolver solver = new StandardLcsSolver();

        String[][] testPairs = {
                {"A", "B"},
                {"AB", "BA"},
                {"AGGTAB", "GXTXAYB"},
                {"HELLO", "HELLO"},
                {"LONGSTRING", "X"}
        };

        for (String[] pair : testPairs) {
            int lcs = solver.solve(new LcsInput(pair[0], pair[1])).getLength();
            int minLen = Math.min(pair[0].length(), pair[1].length());
            assert lcs <= minLen : String.format("Monotonicity violated: %d > %d", lcs, minLen);
            System.out.println(String.format("✓ LCS(%d, %d) = %d <= min(%d, %d)",
                    pair[0].length(), pair[1].length(), lcs, pair[0].length(), pair[1].length()));
        }
        System.out.println();
    }

    /**
     * Property 3: Subsequence - LCS is subsequence of both strings
     */
    static void testSubsequenceProperty() {
        System.out.println("=== Subsequence Property ===\n");

        String[][] testPairs = {
                {"AGGTAB", "GXTXAYB"},
                {"HELLO", "HALLO"},
                {"ABC", "AXBXC"}
        };

        for (String[] pair : testPairs) {
            String lcs = LcsSequenceReconstructor.reconstructLcs(pair[0], pair[1]);
            assert isSubsequence(lcs, pair[0]) : "LCS not subsequence of first string";
            assert isSubsequence(lcs, pair[1]) : "LCS not subsequence of second string";
            System.out.println(String.format("✓ LCS(%s, %s) = %s is subsequence of both",
                    pair[0], pair[1], lcs));
        }
        System.out.println();
    }

    /**
     * Property 4: Idempotency - LCS(s, s) == s
     */
    static void testIdempotencyProperty() {
        System.out.println("=== Idempotency Property ===\n");
        LcsSolver solver = new StandardLcsSolver();

        String[] testStrings = {"", "A", "AB", "AGGTAB", "HELLO", "The quick brown fox"};

        for (String s : testStrings) {
            int lcs = solver.solve(new LcsInput(s, s)).getLength();
            assert lcs == s.length() : String.format("Idempotency violated: %d != %d", lcs, s.length());
            System.out.println("✓ LCS(" + s + ", " + s + ") = " + lcs);
        }
        System.out.println();
    }

    /**
     * Property 5: Composition - LCS(s1, s3) <= LCS(s1, s2) + LCS(s2, s3)
     */
    static void testCompositionProperty() {
        System.out.println("=== Composition Property ===\n");
        LcsSolver solver = new StandardLcsSolver();

        String[] s1 = {"AGGTAB", "HELLO", "ABC"};
        String[] s2 = {"GXTXAYB", "HALLO", "AXBXC"};
        String[] s3 = {"TAXXGGTAB", "HXALLO", "AXBXCXD"};

        for (int i = 0; i < s1.length; i++) {
            int lcs13 = solver.solve(new LcsInput(s1[i], s3[i])).getLength();
            int lcs12 = solver.solve(new LcsInput(s1[i], s2[i])).getLength();
            int lcs23 = solver.solve(new LcsInput(s2[i], s3[i])).getLength();
            int bound = lcs12 + lcs23;

            assert lcs13 <= bound : String.format("Composition violated: %d > %d", lcs13, bound);
            System.out.println(String.format("✓ LCS(%s, %s) = %d <= %d + %d",
                    s1[i], s3[i], lcs13, lcs12, lcs23));
        }
        System.out.println();
    }

    /**
     * Property 6: Scaling - LCS increases monotonically with string similarity
     */
    static void testScalingProperty() {
        System.out.println("=== Scaling Property ===\n");
        LcsSolver solver = new StandardLcsSolver();

        // Create strings with increasing similarity
        String base = "ABCDEFGHIJ";
        int[] lcsLengths = new int[4];

        // Remove increasing number of characters
        String s1 = base;
        String s2 = "XYZWABCDEFGHIJ";  // Has full base
        String s3 = "XYZWABCDXYZ";     // Has partial
        String s4 = "XYZWABXY";        // Even less

        lcsLengths[0] = solver.solve(new LcsInput(s1, s2)).getLength();
        lcsLengths[1] = solver.solve(new LcsInput(s1, s3)).getLength();
        lcsLengths[2] = solver.solve(new LcsInput(s1, s4)).getLength();
        lcsLengths[3] = 0; // Completely different

        for (int i = 0; i < lcsLengths.length - 1; i++) {
            assert lcsLengths[i] >= lcsLengths[i + 1] :
                    String.format("Scaling violated: %d < %d", lcsLengths[i], lcsLengths[i + 1]);
            System.out.println("✓ Similarity decrease: " + lcsLengths[i] + " >= " + lcsLengths[i + 1]);
        }
        System.out.println();
    }

    /**
     * Helper: Check if needle is a subsequence of haystack.
     */
    private static boolean isSubsequence(String needle, String haystack) {
        int j = 0;
        for (int i = 0; i < haystack.length() && j < needle.length(); i++) {
            if (haystack.charAt(i) == needle.charAt(j)) {
                j++;
            }
        }
        return j == needle.length();
    }
}
