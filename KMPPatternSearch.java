import java.util.ArrayList;
import java.util.List;

public class KMPPatternSearch implements PatternSearcher {

    private final String pattern;
    private final int[] failureTable;

    public KMPPatternSearch(String pattern) {
        if (pattern == null || pattern.isEmpty())
            throw new InvalidPatternException(pattern);
        this.pattern = pattern;
        this.failureTable = buildFailureTable(pattern);
    }

    @Override
    public SearchResult search(String text) {
        if (text == null)
            throw new InvalidTextException();

        List<Integer> matches = new ArrayList<>();
        int patternLen = pattern.length();
        int j = 0;

        for (int i = 0; i < text.length(); i++) {
            while (j > 0 && text.charAt(i) != pattern.charAt(j))
                j = failureTable[j - 1];

            if (text.charAt(i) == pattern.charAt(j))
                j++;

            if (j == patternLen) {
                matches.add(i - patternLen + 1);
                j = failureTable[j - 1];
            }
        }

        return new SearchResult(pattern, matches);
    }

    // Builds the longest-proper-prefix-suffix table in O(m).
    // failureTable[i] = length of the longest proper prefix of pattern[0..i]
    // that is also a suffix, allowing the search to skip re-examining characters.
    private static int[] buildFailureTable(String pattern) {
        int m = pattern.length();
        int[] table = new int[m];
        int len = 0;
        int i = 1;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                table[i++] = ++len;
            } else if (len > 0) {
                len = table[len - 1];
            } else {
                table[i++] = 0;
            }
        }

        return table;
    }
}
