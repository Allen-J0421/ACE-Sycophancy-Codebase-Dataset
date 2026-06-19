class TestSpaceOptimized {
    public static void main(String[] args) {
        LcsSolver standard = new StandardLcsSolver();
        LcsSolver spaceOptimized = new SpaceOptimizedLcsSolver();

        String[][] testCases = {
            {"AGGTAB", "GXTXAYB"},
            {"", ""},
            {"ABC", "DEF"},
            {"ABCDEF", "ABCDEF"},
            {"AAAA", "AA"},
        };

        System.out.println("=== Comparing StandardLcsSolver vs SpaceOptimizedLcsSolver ===\n");

        for (String[] testCase : testCases) {
            String s1 = testCase[0];
            String s2 = testCase[1];
            LcsInput input = new LcsInput(s1, s2);

            int standard_result = standard.solve(input).getLength();
            int optimized_result = spaceOptimized.solve(input).getLength();

            String match = (standard_result == optimized_result) ? "✓" : "✗";
            System.out.println(match + " \"" + s1 + "\" vs \"" + s2 + "\"");
            System.out.println("  Standard: " + standard_result + ", SpaceOptimized: " + optimized_result);
        }
    }
}
