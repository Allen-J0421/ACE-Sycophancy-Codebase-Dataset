import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class KMPSearch {

    private final String pattern;
    private final int[] lps;

    KMPSearch(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("pattern must be non-null and non-empty");
        }
        this.pattern = pattern;
        this.lps = computeLPS(pattern);
    }

    private static int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else if (len != 0) {
                len = lps[len - 1];
            } else {
                i++;
            }
        }
        return lps;
    }

    List<Integer> search(String text) {
        if (text == null || pattern.length() > text.length()) {
            return Collections.emptyList();
        }
        int n = text.length();
        int m = pattern.length();
        List<Integer> result = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    result.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j != 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        KMPSearch searcher = new KMPSearch("aaba");
        List<Integer> result = searcher.search("aabaacaadaabaaba");
        System.out.println(result.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));
    }
}
