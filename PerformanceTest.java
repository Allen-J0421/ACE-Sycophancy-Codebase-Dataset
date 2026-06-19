/**
 * Performance test demonstrating the improvement from using append+reverse
 * instead of insert(0, ...) in sequence reconstruction.
 */
class PerformanceTest {

    /**
     * Simulates the OLD inefficient approach using insert(0, ...).
     * This is O(n²) because each insert shifts all existing characters.
     */
    static String reconstructOldWay(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dpTable = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dpTable[i][j] = dpTable[i - 1][j - 1] + 1;
                } else {
                    dpTable[i][j] = Math.max(dpTable[i - 1][j], dpTable[i][j - 1]);
                }
            }
        }

        StringBuilder lcs = new StringBuilder();
        int i = m;
        int j = n;

        while (i > 0 && j > 0) {
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                lcs.insert(0, s1.charAt(i - 1)); // O(n) operation!
                i--;
                j--;
            } else if (dpTable[i - 1][j] > dpTable[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }

        return lcs.toString();
    }

    /**
     * NEW efficient approach using append + reverse.
     * This is O(n) because append is O(1) amortized, and reverse is O(n).
     */
    static String reconstructNewWay(String s1, String s2) {
        return LcsSequenceReconstructor.reconstructLcs(s1, s2);
    }

    public static void main(String[] args) {
        System.out.println("=== StringBuilder Performance Comparison ===\n");

        String[][] testCases = {
                generateString('A', 100, 'B', 100),   // 100x100
                generateString('A', 500, 'B', 500),   // 500x500
                generateString('A', 1000, 'B', 1000), // 1000x1000
        };

        for (String[] pair : testCases) {
            String s1 = pair[0];
            String s2 = pair[1];

            System.out.println("Test: " + s1.length() + "x" + s2.length() + " strings");

            // Time the old way
            long startOld = System.nanoTime();
            String resultOld = reconstructOldWay(s1, s2);
            long timeOld = System.nanoTime() - startOld;

            // Time the new way
            long startNew = System.nanoTime();
            String resultNew = reconstructNewWay(s1, s2);
            long timeNew = System.nanoTime() - startNew;

            // Verify correctness
            assert resultOld.equals(resultNew) :
                    "Results differ! Old: " + resultOld + ", New: " + resultNew;

            double ratio = (double) timeOld / timeNew;
            System.out.printf("  Old way (insert):  %,d ns%n", timeOld);
            System.out.printf("  New way (reverse): %,d ns%n", timeNew);
            System.out.printf("  Improvement:       %.1fx faster%n\n", ratio);
        }
    }

    private static String[] generateString(char c1, int len1, char c2, int len2) {
        StringBuilder s1 = new StringBuilder();
        StringBuilder s2 = new StringBuilder();

        for (int i = 0; i < len1; i++) {
            s1.append(c1);
        }
        for (int i = 0; i < len2; i++) {
            s2.append(c2);
        }

        return new String[]{s1.toString(), s2.toString()};
    }
}
