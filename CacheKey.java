import java.util.Objects;

/**
 * Robust cache key for LCS computation results.
 * Normalizes string pairs to enable symmetric caching (handles both (A,B) and (B,A)).
 *
 * Uses length-prefixed encoding to prevent collisions from delimiter ambiguity.
 */
class CacheKey {
    private final String s1;
    private final String s2;
    private final int hashCode;

    /**
     * Creates a normalized cache key from two strings.
     * Automatically sorts strings lexicographically for symmetry.
     *
     * @param s1 first string
     * @param s2 second string
     */
    CacheKey(String s1, String s2) {
        // Normalize order for symmetry: always use same ordering
        if (s1.compareTo(s2) <= 0) {
            this.s1 = s1;
            this.s2 = s2;
        } else {
            this.s1 = s2;
            this.s2 = s1;
        }
        // Pre-compute hash for efficiency
        this.hashCode = Objects.hash(this.s1, this.s2);
    }

    /**
     * Generates a string representation using length-prefixed encoding.
     * This prevents collisions from cases like:
     *   ("AB||CD", "EF") vs ("AB", "||CD||EF")
     *
     * @return encoded cache key string
     */
    String getEncodedKey() {
        return s1.length() + ":" + s1 + "|" + s2.length() + ":" + s2;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CacheKey)) return false;
        CacheKey other = (CacheKey) o;
        return s1.equals(other.s1) && s2.equals(other.s2);
    }

    @Override
    public String toString() {
        return "CacheKey{" +
                "s1='" + s1 + '\'' +
                ", s2='" + s2 + '\'' +
                '}';
    }
}
