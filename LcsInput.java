public class LcsInput {
    private final String first;
    private final String second;

    public LcsInput(String first, String second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Input strings must not be null");
        }
        this.first = first;
        this.second = second;
    }

    public String getFirst() { return first; }
    public String getSecond() { return second; }
}
