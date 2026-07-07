public class PatternSearchFactory {

    public enum Algorithm {
        NAIVE,
        KMP
    }

    public static PatternSearcher create(String pattern, Algorithm algorithm) {
        switch (algorithm) {
            case NAIVE: return new NaivePatternSearch(pattern);
            case KMP:   return new KMPPatternSearch(pattern);
            default: throw new PatternSearchException("Unknown algorithm: " + algorithm);
        }
    }

    public static PatternSearcher create(String pattern) {
        return create(pattern, Algorithm.NAIVE);
    }
}
