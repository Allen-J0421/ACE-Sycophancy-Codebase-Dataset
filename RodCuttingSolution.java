import java.util.List;

class RodCuttingSolution {
    private final int maxRevenue;
    private final List<Integer> cuts;

    RodCuttingSolution(int maxRevenue, List<Integer> cuts) {
        this.maxRevenue = maxRevenue;
        this.cuts = cuts;
    }

    int maxRevenue() {
        return maxRevenue;
    }

    List<Integer> cuts() {
        return cuts;
    }
}
