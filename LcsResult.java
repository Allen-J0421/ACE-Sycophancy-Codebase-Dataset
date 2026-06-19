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
    public String toString() {
        return String.format("LCS length: %d, subsequence: \"%s\"", length, subsequence);
    }
}
