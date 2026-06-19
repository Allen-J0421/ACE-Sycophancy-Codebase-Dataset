public class LcsResult {
    private final String subsequence;

    public LcsResult(String subsequence) {
        if (subsequence == null) throw new IllegalArgumentException("subsequence must not be null");
        this.subsequence = subsequence;
    }

    public int getLength() { return subsequence.length(); }
    public String getSubsequence() { return subsequence; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LcsResult)) return false;
        return subsequence.equals(((LcsResult) o).subsequence);
    }

    @Override
    public int hashCode() {
        return subsequence.hashCode();
    }

    @Override
    public String toString() {
        return String.format("LCS length: %d, subsequence: \"%s\"", getLength(), subsequence);
    }
}
