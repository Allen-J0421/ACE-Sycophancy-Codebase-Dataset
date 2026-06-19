import java.util.ArrayList;
import java.util.List;

/**
 * Knuth-Morris-Pratt (KMP) substring search.
 *
 * <p>Finds all starting indices where a fixed pattern occurs in a text string.
 * The pattern is compiled once at construction time into a failure function (LPS table),
 * making repeated searches against different texts efficient.
 *
 * <p><b>Complexity:</b>
 * <ul>
 *   <li>Construction: O(m) time and space, where m = pattern length</li>
 *   <li>Search: O(n) time, where n = text length</li>
 * </ul>
 *
 * <p><b>Usage:</b>
 * <pre>
 *   KMPSearch searcher = new KMPSearch("aaba");
 *   List&lt;Integer&gt; indices = searcher.search("aabaacaadaabaaba");
 *   // indices → [0, 9, 12]
 * </pre>
 */
class KMPSearch {

    private final String pattern;
    private final int[] lps;

    KMPSearch(String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern must not be null");
        }
        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("pattern must not be empty");
        }
        this.pattern = pattern;
        this.lps = computeLPS();
    }

    private int[] computeLPS() {
        int m = pattern.length();
        int[] table = new int[m];
        int prefixLen = 0;
        int i = 1;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(prefixLen)) {
                prefixLen++;
                table[i] = prefixLen;
                i++;
            } else if (prefixLen != 0) {
                prefixLen = table[prefixLen - 1];
            } else {
                i++;
            }
        }
        return table;
    }

    List<Integer> search(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }
        if (pattern.length() > text.length()) {
            return List.of();
        }
        List<Integer> result = new ArrayList<>();
        int textIdx = 0;
        int patternIdx = 0;
        while (textIdx < text.length()) {
            if (text.charAt(textIdx) == pattern.charAt(patternIdx)) {
                textIdx++;
                patternIdx++;
                if (patternIdx == lps.length) {
                    result.add(textIdx - patternIdx);
                    patternIdx = lps[patternIdx - 1];
                }
            } else if (patternIdx != 0) {
                patternIdx = lps[patternIdx - 1];
            } else {
                textIdx++;
            }
        }
        return List.copyOf(result);
    }

    public static void main(String[] args) {
        KMPSearch searcher = new KMPSearch("aaba");
        List<Integer> result = searcher.search("aabaacaadaabaaba");
        System.out.println(result);
    }
}
