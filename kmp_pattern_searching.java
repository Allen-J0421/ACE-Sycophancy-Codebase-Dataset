import java.util.ArrayList;
import java.util.List;

interface PatternMatcher {
    List<Integer> search(String text);
}

interface SearchEngine {
    List<Integer> execute(String text, PatternContext context);
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

class PatternContext {
    final String pattern;
    final LPSTable lpsTable;

    PatternContext(String pattern) {
        this.pattern = pattern;
        this.lpsTable = new LPSTable(pattern);
    }
}

class KMPCursor {
    private final PatternContext context;
    private int j = 0;

    KMPCursor(PatternContext context) {
        this.context = context;
    }

    int advance(char c, int textPos) {
        while (j > 0 && c != context.pattern.charAt(j))
            j = context.lpsTable.at(j - 1);
        if (c == context.pattern.charAt(j))
            j++;
        if (j == context.pattern.length()) {
            int matchStart = textPos - context.pattern.length() + 1;
            j = context.lpsTable.at(j - 1);
            return matchStart;
        }
        return -1;
    }
}

class KMPSearchEngine implements SearchEngine {
    @Override
    public List<Integer> execute(String text, PatternContext context) {
        List<Integer> results = new ArrayList<>();
        KMPCursor cursor = new KMPCursor(context);
        for (int i = 0; i < text.length(); i++) {
            int match = cursor.advance(text.charAt(i), i);
            if (match >= 0)
                results.add(match);
        }
        return results;
    }
}

class KMPSearch implements PatternMatcher {
    private final PatternContext context;
    private final SearchEngine engine;

    KMPSearch(String pattern, SearchEngine engine) {
        this.context = new PatternContext(pattern);
        this.engine = engine;
    }

    @Override
    public List<Integer> search(String text) {
        return engine.execute(text, context);
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
