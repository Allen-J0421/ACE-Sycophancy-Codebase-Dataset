package naivepatternsearch;

public final class PatternSearchers {

    private PatternSearchers() {
        // Utility class.
    }

    public static PatternSearcher naive() {
        return NaivePatternSearch.INSTANCE;
    }
}
