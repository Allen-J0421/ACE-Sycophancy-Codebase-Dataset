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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LcsInput)) return false;
        LcsInput other = (LcsInput) o;
        return first.equals(other.first) && second.equals(other.second);
    }

    @Override
    public int hashCode() {
        return 31 * first.hashCode() + second.hashCode();
    }
}
