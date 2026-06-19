import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        System.out.println(result.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));
    }
}
