public class LcsResult {
    private final int length;
    private final String subsequence;

    public LcsResult(int length, String subsequence) {
        this.length = length;
        this.subsequence = subsequence;
    }

    public int getLength() { return length; }
    public String getSubsequence() { return subsequence; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LcsResult)) return false;
        LcsResult other = (LcsResult) o;
        return length == other.length && subsequence.equals(other.subsequence);
    }

    @Override
    public int hashCode() {
        return 31 * length + subsequence.hashCode();
    }

    @Override
    public String toString() {
        return String.format("LCS length: %d, subsequence: \"%s\"", length, subsequence);
    }
}
