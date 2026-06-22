import java.util.List;

record RodCuttingSolution(int maxRevenue, List<Integer> cuts) {
    RodCuttingSolution {
        if (maxRevenue < 0) {
            throw new IllegalArgumentException("Maximum revenue must not be negative.");
        }

        cuts = List.copyOf(cuts);
    }
}
