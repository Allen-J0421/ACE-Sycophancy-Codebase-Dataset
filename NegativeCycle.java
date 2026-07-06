import java.util.List;

record NegativeCycle(List<Integer> vertices) implements ShortestPathResult {

    @Override
    public <T> T accept(ResultVisitor<T> visitor) {
        return visitor.onNegativeCycle(this);
    }

    @Override
    public String toString() {
        return "NegativeCycle[vertices=" + vertices + "]";
    }
}
