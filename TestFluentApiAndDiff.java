/**
 * Tests for fluent API and diffing utilities.
 */
class TestFluentApiAndDiff {

    public static void main(String[] args) {
        testFluentComparison();
        testBatchQueries();
        testComprehensiveAnalysis();
        testDiffing();
        System.out.println("\n✓ All fluent API and diff tests passed");
    }

    static void testFluentComparison() {
        System.out.println("=== Fluent Comparison API ===\n");

        // Test basic length
        System.out.println("Test 1: Get LCS length");
        int length = LcsQueries.compare("AGGTAB", "GXTXAYB").length();
        assert length == 4;
        System.out.println("✓ Length: " + length);

        // Test sequence
        System.out.println("\nTest 2: Get LCS sequence");
        String seq = LcsQueries.compare("AGGTAB", "GXTXAYB").sequence();
        assert seq.length() == 4;
        System.out.println("✓ Sequence: " + seq);

        // Test similarity
        System.out.println("\nTest 3: Get similarity ratio");
        double sim = LcsQueries.compare("AGGTAB", "GXTXAYB").similarity();
        assert sim > 60 && sim < 70;
        System.out.println(String.format("✓ Similarity: %.1f%%", sim));

        // Test with cache
        System.out.println("\nTest 4: Cached comparison");
        double sim2 = LcsQueries.compare("HELLO", "HELLO").cached().similarity();
        assert sim2 == 100.0;
        System.out.println("✓ Cached identical strings: " + sim2 + "%");

        // Test case-insensitive
        System.out.println("\nTest 5: Case-insensitive comparison");
        int lenCI = LcsQueries.compare("HELLO", "hello").caseInsensitive().length();
        assert lenCI == 5;
        System.out.println("✓ Case-insensitive match: " + lenCI);

        // Test edit distance
        System.out.println("\nTest 6: Estimated edit distance");
        int editDist = LcsQueries.compare("ABC", "AXC").editDistance();
        assert editDist == 1;
        System.out.println("✓ Edit distance: " + editDist);

        // Test similarity threshold
        System.out.println("\nTest 7: Similarity threshold check");
        boolean meets80 = LcsQueries.compare("AGGTAB", "GGTAB").meetsSimilarity(80);
        assert meets80;
        System.out.println("✓ Meets 80% threshold");

        System.out.println();
    }

    static void testBatchQueries() {
        System.out.println("=== Batch Queries ===\n");

        // Test batch lengths
        System.out.println("Test 1: Get multiple LCS lengths");
        int[] lengths = LcsQueries.batch("ABC", "BCD", "HELLO", "HALLO")
                .lengths();
        assert lengths.length == 2;
        assert lengths[0] == 2; // ABC vs BCD
        assert lengths[1] == 4; // HELLO vs HALLO
        System.out.println("✓ Batch lengths: " + java.util.Arrays.toString(lengths));

        // Test batch sequences
        System.out.println("\nTest 2: Get multiple LCS sequences");
        String[] seqs = LcsQueries.batch("ABC", "BCD", "AGGTAB", "GXTXAYB")
                .sequences();
        assert seqs.length == 2;
        System.out.println("✓ Batch sequences: " + seqs[0] + ", " + seqs[1]);

        // Test average similarity
        System.out.println("\nTest 3: Average similarity");
        double avgSim = LcsQueries.batch("AGGTAB", "AGGTAB", "ABC", "DEF")
                .averageSimilarity();
        assert avgSim > 40 && avgSim < 60; // One perfect match, one no match
        System.out.println(String.format("✓ Average similarity: %.1f%%", avgSim));

        System.out.println();
    }

    static void testComprehensiveAnalysis() {
        System.out.println("=== Comprehensive Analysis ===\n");

        System.out.println("Analysis of AGGTAB vs GXTXAYB:");
        LcsQueries.ComprehensiveAnalysis analysis = LcsQueries.analyze("AGGTAB", "GXTXAYB");
        System.out.println(analysis);
        assert analysis.lcsLength == 4;
        assert analysis.lcsSequence.length() == 4;
        System.out.println("\n✓ Comprehensive analysis complete");

        System.out.println();
    }

    static void testDiffing() {
        System.out.println("=== String Diffing ===\n");

        String s1 = "AGGTAB";
        String s2 = "GXTXAYB";
        String lcs = "GTAB";

        // Test visual diff
        System.out.println("Test 1: Visual diff");
        String visualDiff = LcsDiffer.visualDiff(s1, s2, lcs);
        assert visualDiff.contains("String 1");
        assert visualDiff.contains("LCS");
        System.out.println("✓ Visual diff generated");

        // Test ASCII diff
        System.out.println("\nTest 2: ASCII diff");
        String asciiDiff = LcsDiffer.asciiDiff(s1, s2, lcs);
        assert asciiDiff.contains("=") || asciiDiff.contains("+") || asciiDiff.contains("-");
        System.out.println("✓ ASCII diff generated");

        // Test side-by-side diff
        System.out.println("\nTest 3: Side-by-side diff");
        String sideBySide = LcsDiffer.sideBySideDiff(s1, s2, lcs);
        assert sideBySide.contains("String 1");
        assert sideBySide.contains("String 2");
        System.out.println("✓ Side-by-side diff generated");

        // Test markdown diff
        System.out.println("\nTest 4: Markdown diff");
        String markdownDiff = LcsDiffer.markdownDiff(s1, s2, lcs);
        assert markdownDiff.contains("##");
        assert markdownDiff.contains("**");
        System.out.println("✓ Markdown diff generated");

        System.out.println();
    }
}
