import java.util.List;

record NegativeCycle(List<Integer> vertices) implements ShortestPathResult {

    @Override
    public String toString() {
        return "NegativeCycle[vertices=" + vertices + "]";
    }
}
