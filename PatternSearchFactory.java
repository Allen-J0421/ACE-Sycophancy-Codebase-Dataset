public class PatternSearchFactory {

    public enum Algorithm {
        NAIVE
    }

    public static PatternSearcher create(String pattern, Algorithm algorithm) {
        switch (algorithm) {
            case NAIVE: return new NaivePatternSearch(pattern);
            default: throw new PatternSearchException("Unknown algorithm: " + algorithm);
        }
    }

    public static PatternSearcher create(String pattern) {
        return create(pattern, Algorithm.NAIVE);
    }
}
