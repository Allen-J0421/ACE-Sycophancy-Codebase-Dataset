/**
 * Tests for specialized LCS solver variants.
 */
class TestSpecializedSolvers {

    public static void main(String[] args) {
        testApproximateLcsSolver();
        testSubstringLcsSolver();
        System.out.println("\n✓ All specialized solver tests passed");
    }

    static void testApproximateLcsSolver() {
        System.out.println("=== Approximate LCS Solver ===\n");

        LcsSolver approximate = new ApproximateLcsSolver();
        LcsSolver standard = new StandardLcsSolver();

        // Test 1: Small input (should use exact algorithm)
        System.out.println("Test 1: Small input (exact algorithm)");
        LcsInput input1 = new LcsInput("ABC", "BCD");
        int approxResult1 = approximate.solve(input1).getLength();
        int standardResult1 = standard.solve(input1).getLength();
        assert approxResult1 == standardResult1 : "Small inputs should match";
        System.out.println("✓ Small input: " + approxResult1 + " (matches standard)");

        // Test 2: Similar strings
        System.out.println("\nTest 2: Similar strings (90% match)");
        String s1 = "The quick brown fox jumps over the lazy dog";
        String s2 = "The quack brown fox jumps over the lazy dog"; // one char different
        LcsInput input2 = new LcsInput(s1, s2);
        int approxResult2 = approximate.solve(input2).getLength();
        int standardResult2 = standard.solve(input2).getLength();
        System.out.println("Approximate: " + approxResult2 + ", Standard: " + standardResult2);
        // Allow small difference due to approximation
        assert Math.abs(approxResult2 - standardResult2) <= 2 : "Results should be close";
        System.out.println("✓ Similar strings: approximate is close to standard");

        // Test 3: No match
        System.out.println("\nTest 3: No match");
        LcsInput input3 = new LcsInput("AAA", "BBB");
        int approxResult3 = approximate.solve(input3).getLength();
        int standardResult3 = standard.solve(input3).getLength();
        assert approxResult3 == standardResult3 && approxResult3 == 0 : "No match case";
        System.out.println("✓ No match: " + approxResult3);

        // Test 4: Complete match
        System.out.println("\nTest 4: Complete match");
        LcsInput input4 = new LcsInput("AGGTAB", "AGGTAB");
        int approxResult4 = approximate.solve(input4).getLength();
        assert approxResult4 == 6 : "Complete match";
        System.out.println("✓ Complete match: " + approxResult4);

        System.out.println();
    }

    static void testSubstringLcsSolver() {
        System.out.println("=== Substring LCS Solver ===\n");

        LcsSolver substring = new SubstringLcsSolver();
        LcsSolver standard = new StandardLcsSolver();

        // Test 1: Small pattern in large text
        System.out.println("Test 1: Pattern matching (small pattern)");
        String pattern = "AGGTAB";
        String text = "XXAGGTABXX"; // Pattern with noise
        LcsInput input1 = new LcsInput(pattern, text);
        int substringResult1 = substring.solve(input1).getLength();
        int standardResult1 = standard.solve(input1).getLength();
        assert substringResult1 == standardResult1 && substringResult1 == 6;
        System.out.println("✓ Pattern found: " + substringResult1);

        // Test 2: Pattern not completely in text
        System.out.println("\nTest 2: Partial pattern match");
        String pattern2 = "AGGTAB";
        String text2 = "AGTBXXXXX"; // Missing some chars from pattern
        LcsInput input2 = new LcsInput(pattern2, text2);
        int substringResult2 = substring.solve(input2).getLength();
        int standardResult2 = standard.solve(input2).getLength();
        assert substringResult2 == standardResult2;
        System.out.println("✓ Partial match: " + substringResult2 + " chars found");

        // Test 3: Pattern in noisy text
        System.out.println("\nTest 3: Pattern with noise");
        String pattern3 = "HELLO";
        String text3 = "HXEXLXLXO"; // Same chars, noise in between
        LcsInput input3 = new LcsInput(pattern3, text3);
        int substringResult3 = substring.solve(input3).getLength();
        int standardResult3 = standard.solve(input3).getLength();
        assert substringResult3 == standardResult3 && substringResult3 == 5;
        System.out.println("✓ Pattern in noise: " + substringResult3);

        // Test 4: Multiple pattern instances
        System.out.println("\nTest 4: Repeated pattern");
        String pattern4 = "AB";
        String text4 = "AXBXAXBX"; // Pattern appears multiple times with noise
        LcsInput input4 = new LcsInput(pattern4, text4);
        int substringResult4 = substring.solve(input4).getLength();
        int standardResult4 = standard.solve(input4).getLength();
        assert substringResult4 == standardResult4 && substringResult4 == 2;
        System.out.println("✓ Repeated pattern: " + substringResult4);

        // Test 5: SubstringLcsResult with complete match
        System.out.println("\nTest 5: SubstringLcsResult complete match");
        SubstringLcsResult result1 = new SubstringLcsResult(6, 6, 10);
        assert result1.isCompleteMatch() : "Should be complete match";
        assert result1.coverage == 100.0 : "Coverage should be 100%";
        System.out.println("✓ Complete match detected: " + result1);

        // Test 6: SubstringLcsResult partial match with threshold
        System.out.println("\nTest 6: SubstringLcsResult with threshold");
        SubstringLcsResult result2 = new SubstringLcsResult(4, 6, 10);
        assert !result2.isCompleteMatch() : "Should not be complete";
        assert result2.meetsCoverage(50.0) : "Should meet 50% threshold";
        assert !result2.meetsCoverage(75.0) : "Should not meet 75% threshold";
        System.out.println("✓ Partial match with thresholds: " + result2);

        System.out.println();
    }
}
