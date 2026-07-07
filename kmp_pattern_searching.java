import java.util.ArrayList;
import java.util.List;

interface PatternMatcher {
    List<Integer> search(String text);
}

interface SearchEngine {
    List<Integer> execute(String text, PatternContext context);
}

interface KMPCursor {
    // Returns the match start index when a full match completes, -1 otherwise.
    int advance(char c, int textPos);
}

interface CursorFactory {
    KMPCursor create(PatternContext context);
}

interface LPSTableFactory {
    LPSTable create(String pattern);
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

class DefaultLPSTableFactory implements LPSTableFactory {
    @Override
    public LPSTable create(String pattern) {
        return new LPSTable(pattern);
    }
}

class PatternContext {
    final String pattern;
    final LPSTable lpsTable;

    PatternContext(String pattern, LPSTableFactory lpsTableFactory) {
        this.pattern = pattern;
        this.lpsTable = lpsTableFactory.create(pattern);
    }
}

class DefaultKMPCursor implements KMPCursor {
    private final PatternContext context;
    private int j = 0;

    DefaultKMPCursor(PatternContext context) {
        this.context = context;
    }

    @Override
    public int advance(char c, int textPos) {
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

class DefaultCursorFactory implements CursorFactory {
    @Override
    public KMPCursor create(PatternContext context) {
        return new DefaultKMPCursor(context);
    }
}

class KMPSearchEngine implements SearchEngine {
    private final CursorFactory cursorFactory;

    KMPSearchEngine(CursorFactory cursorFactory) {
        this.cursorFactory = cursorFactory;
    }

    @Override
    public List<Integer> execute(String text, PatternContext context) {
        List<Integer> results = new ArrayList<>();
        KMPCursor cursor = cursorFactory.create(context);
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

    KMPSearch(PatternContext context, SearchEngine engine) {
        this.context = context;
        this.engine = engine;
    }

    @Override
    public List<Integer> search(String text) {
        return engine.execute(text, context);
    }
}

class KMPMatcherBuilder {
    private String pattern;
    private LPSTableFactory lpsTableFactory = new DefaultLPSTableFactory();
    private CursorFactory cursorFactory = new DefaultCursorFactory();

    KMPMatcherBuilder pattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    KMPMatcherBuilder lpsTableFactory(LPSTableFactory lpsTableFactory) {
        this.lpsTableFactory = lpsTableFactory;
        return this;
    }

    KMPMatcherBuilder cursorFactory(CursorFactory cursorFactory) {
        this.cursorFactory = cursorFactory;
        return this;
    }

    PatternMatcher build() {
        PatternContext context = new PatternContext(pattern, lpsTableFactory);
        SearchEngine engine = new KMPSearchEngine(cursorFactory);
        return new KMPSearch(context, engine);
    }
}

class PatternMatcherFactory {
    static PatternMatcher createKMP(String pattern) {
        return new KMPMatcherBuilder().pattern(pattern).build();
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
