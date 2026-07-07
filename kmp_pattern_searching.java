import java.util.ArrayList;
import java.util.List;

interface PatternMatcher {
    List<Integer> search(String text);
}

class LPSTable {
    private final int[] lps;

    LPSTable(String pattern) {
        lps = new int[pattern.length()];
        build(pattern);
    }

    private void build(String pattern) {
        int len = 0;
        int i = 1;
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                lps[i++] = ++len;
            } else if (len != 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
    }

    int at(int i) {
        return lps[i];
    }
}

class KMPSearch implements PatternMatcher {
    private final String pattern;
    private final LPSTable lpsTable;

    KMPSearch(String pattern) {
        this.pattern = pattern;
        this.lpsTable = new LPSTable(pattern);
    }

    @Override
    public List<Integer> search(String text) {
        List<Integer> results = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        int i = 0, j = 0;
        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    results.add(i - j);
                    j = lpsTable.at(j - 1);
                }
            } else if (j != 0) {
                j = lpsTable.at(j - 1);
            } else {
                i++;
            }
        }
        return results;
    }

    public static void main(String[] args) {
        String txt = "aabaacaadaabaaba";
        String pat = "aaba";
        PatternMatcher matcher = new KMPSearch(pat);
        List<Integer> res = matcher.search(txt);
        for (int i = 0; i < res.size(); i++)
            System.out.print(res.get(i) + " ");
    }
}
