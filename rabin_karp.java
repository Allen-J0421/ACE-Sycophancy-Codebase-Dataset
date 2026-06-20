import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rabin-Karp substring search.
 *
 * <p>Finds every starting index at which {@code pattern} occurs in {@code text}
 * using a rolling polynomial hash, which lets each window be compared in O(1)
 * amortised time with an O(m) verification only on a hash collision.
 *
 * <p>The implementation is split into small, reusable pieces:
 * <ul>
 *   <li>{@link RollingHash} - the polynomial hash arithmetic (base, modulus,
 *       and the roll operation), with no knowledge of matching.</li>
 *   <li>{@link CompiledPattern} - a pattern hashed once so it can be searched
 *       in many texts without recomputing its hash.</li>
 *   <li>{@code search} - the convenience entry points.</li>
 * </ul>
 */
class RabinKarp {

    /** Default alphabet size; covers the full range of {@code char}. */
    private static final int DEFAULT_BASE = 256;

    /** Default modulus; a prime keeps hashes well distributed. */
    private static final int DEFAULT_MODULUS = 101;

    private RabinKarp() {
        // Utility class: use the static search methods or CompiledPattern.
    }

    /**
     * Returns the starting indices of every occurrence of {@code pattern} in
     * {@code text}, using the default base and modulus.
     */
    static List<Integer> search(String pattern, String text) {
        return new CompiledPattern(pattern, DEFAULT_BASE, DEFAULT_MODULUS).findIn(text);
    }

    /**
     * A polynomial rolling hash over a window of fixed length.
     *
     * <p>The hash of the characters {@code c0 c1 ... c(m-1)} is
     * {@code (c0*base^(m-1) + c1*base^(m-2) + ... + c(m-1)) mod modulus}.
     * {@link #roll} advances the window by one character in O(1).
     */
    static final class RollingHash {

        private final int base;
        private final int modulus;
        /** {@code base^(windowLength-1) mod modulus}, the weight of the leading char. */
        private final long leadingWeight;

        // Hashes use long arithmetic so intermediate products stay exact even
        // for large (collision-resistant) moduli, where int math would overflow.
        private long value;

        RollingHash(int base, int modulus, int windowLength) {
            if (modulus <= 0) {
                throw new IllegalArgumentException("modulus must be positive: " + modulus);
            }
            this.base = base;
            this.modulus = modulus;

            long weight = 1;
            for (int i = 0; i < windowLength - 1; i++) {
                weight = (weight * base) % modulus;
            }
            this.leadingWeight = weight;
        }

        /** Folds one more character into the (growing) hash. */
        void append(char c) {
            value = (base * value + c) % modulus;
        }

        /** The current hash value. */
        long value() {
            return value;
        }

        /**
         * Slides the window: drops {@code outgoing} from the front and adds
         * {@code incoming} at the back. The window length must match the value
         * passed to the constructor.
         */
        void roll(char outgoing, char incoming) {
            value = (base * (value - outgoing * leadingWeight) + incoming) % modulus;
            if (value < 0) {
                value += modulus;
            }
        }
    }

    /**
     * A pattern whose hash has been computed once so it can be searched for in
     * many texts. Instances are immutable and safe to reuse.
     */
    static final class CompiledPattern {

        private final String pattern;
        private final int base;
        private final int modulus;
        private final long patternHash;

        CompiledPattern(String pattern, int base, int modulus) {
            if (pattern == null) {
                throw new IllegalArgumentException("pattern must not be null");
            }
            this.pattern = pattern;
            this.base = base;
            this.modulus = modulus;

            RollingHash hash = new RollingHash(base, modulus, pattern.length());
            for (int i = 0; i < pattern.length(); i++) {
                hash.append(pattern.charAt(i));
            }
            this.patternHash = hash.value();
        }

        /** Returns the starting indices of every occurrence of this pattern in {@code text}. */
        List<Integer> findIn(String text) {
            if (text == null) {
                throw new IllegalArgumentException("text must not be null");
            }

            int m = pattern.length();
            int n = text.length();
            // An empty pattern or one longer than the text yields no matches.
            if (m == 0 || m > n) {
                return new ArrayList<>();
            }

            List<Integer> matches = new ArrayList<>();
            RollingHash window = new RollingHash(base, modulus, m);
            for (int i = 0; i < m; i++) {
                window.append(text.charAt(i));
            }

            for (int i = 0; i <= n - m; i++) {
                if (window.value() == patternHash && regionMatches(text, i)) {
                    matches.add(i);
                }
                if (i < n - m) {
                    window.roll(text.charAt(i), text.charAt(i + m));
                }
            }
            return Collections.unmodifiableList(matches);
        }

        /** Confirms a hash hit by comparing the pattern against text at {@code start}. */
        private boolean regionMatches(String text, int start) {
            for (int j = 0; j < pattern.length(); j++) {
                if (text.charAt(start + j) != pattern.charAt(j)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {
        String text = "geeksforgeeks";
        String pattern = "geeks";

        List<Integer> result = search(pattern, text);

        StringBuilder out = new StringBuilder();
        for (int index : result) {
            out.append(index).append(' ');
        }
        System.out.println(out.toString().trim());
    }
}
