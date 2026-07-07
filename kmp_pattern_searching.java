import java.util.ArrayList;
import java.util.List;

interface PatternMatcher {
    List<Integer> search(String text);
}

interface SearchEngine {
    List<Integer> execute(String text, String pattern, LPSTable lpsTable);
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

class KMPCursor {
    private final String pattern;
    private final LPSTable lpsTable;
    private int j = 0;

    KMPCursor(String pattern, LPSTable lpsTable) {
        this.pattern = pattern;
        this.lpsTable = lpsTable;
    }

    // Returns the match start index when a full match completes, -1 otherwise.
    int advance(char c, int textPos) {
        while (j > 0 && c != pattern.charAt(j))
            j = lpsTable.at(j - 1);
        if (c == pattern.charAt(j))
            j++;
        if (j == pattern.length()) {
            int matchStart = textPos - pattern.length() + 1;
            j = lpsTable.at(j - 1);
            return matchStart;
        }
        return -1;
    }
}

class KMPSearchEngine implements SearchEngine {
    @Override
    public List<Integer> execute(String text, String pattern, LPSTable lpsTable) {
        List<Integer> results = new ArrayList<>();
        KMPCursor cursor = new KMPCursor(pattern, lpsTable);
        for (int i = 0; i < text.length(); i++) {
            int match = cursor.advance(text.charAt(i), i);
            if (match >= 0)
                results.add(match);
        }
        return results;
    }
}

class KMPSearch implements PatternMatcher {
    private final String pattern;
    private final LPSTable lpsTable;
    private final SearchEngine engine;

    KMPSearch(String pattern, SearchEngine engine) {
        this.pattern = pattern;
        this.lpsTable = new LPSTable(pattern);
        this.engine = engine;
    }

    @Override
    public List<Integer> search(String text) {
        return engine.execute(text, pattern, lpsTable);
    }
}

class PatternMatcherFactory {
    static PatternMatcher createKMP(String pattern) {
        return new KMPSearch(pattern, new KMPSearchEngine());
    }
}

class Main {
    public static void main(String[] args) {
        String txt = "aabaacaadaabaaba";
        String pat = "aaba";
        PatternMatcher matcher = PatternMatcherFactory.createKMP(pat);
        List<Integer> res = matcher.search(txt);
        for (int i = 0; i < res.size(); i++)
            System.out.print(res.get(i) + " ");
    }
}
