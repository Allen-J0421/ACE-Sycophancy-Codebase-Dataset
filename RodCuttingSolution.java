import java.util.ArrayList;
import java.util.List;

class RodCuttingSolution {
    private final int revenue;
    private final List<Integer> cuts;

    RodCuttingSolution(int revenue, List<Integer> cuts) {
        this.revenue = revenue;
        this.cuts = new ArrayList<>(cuts);
    }

    int revenue() {
        return revenue;
    }

    List<Integer> cuts() {
        return new ArrayList<>(cuts);
    }

    @Override
    public String toString() {
        return "Revenue: " + revenue + ", Cuts: " + cuts;
    }
}
