import java.util.ArrayList;
import java.util.List;

public class NaivePatternSearch implements PatternSearcher {

    private final String pattern;

    public NaivePatternSearch(String pattern) {
        if (pattern == null || pattern.isEmpty())
            throw new IllegalArgumentException("Pattern must be non-empty");
        this.pattern = pattern;
    }

    @Override
    public List<Integer> search(String text) {
        if (text == null)
            throw new IllegalArgumentException("Text must not be null");

        List<Integer> matches = new ArrayList<>();
        int patternLen = pattern.length();
        int textLen = text.length();

        for (int i = 0; i <= textLen - patternLen; i++) {
            if (matchesAt(text, i))
                matches.add(i);
        }

        return matches;
    }

    private boolean matchesAt(String text, int startIndex) {
        for (int i = 0; i < pattern.length(); i++) {
            if (text.charAt(startIndex + i) != pattern.charAt(i))
                return false;
        }
        return true;
    }
}
