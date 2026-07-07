import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NaivePatternSearch implements PatternSearcher {

    private final String pattern;

    public NaivePatternSearch(String pattern) {
        if (pattern == null || pattern.isEmpty())
            throw new InvalidPatternException(pattern);
        this.pattern = pattern;
    }

    @Override
    public SearchResult search(String text) {
        if (text == null)
            throw new InvalidTextException();

        List<Integer> matches = new ArrayList<>();
        int patternLen = pattern.length();
        int textLen = text.length();

        for (int i = 0; i <= textLen - patternLen; i++) {
            if (matchesAt(text, i))
                matches.add(i);
        }

        return new SearchResult(pattern, matches);
    }

    @Override
    public Stream<Integer> stream(String text) {
        if (text == null)
            throw new InvalidTextException();

        int limit = text.length() - pattern.length();
        return IntStream.rangeClosed(0, limit)
                        .filter(i -> matchesAt(text, i))
                        .boxed();
    }

    private boolean matchesAt(String text, int startIndex) {
        for (int i = 0; i < pattern.length(); i++) {
            if (text.charAt(startIndex + i) != pattern.charAt(i))
                return false;
        }
        return true;
    }
}
