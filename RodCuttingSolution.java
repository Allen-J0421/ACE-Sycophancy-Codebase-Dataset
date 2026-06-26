import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        return Collections.unmodifiableList(cuts);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RodCuttingSolution)) return false;
        RodCuttingSolution other = (RodCuttingSolution) obj;
        return revenue == other.revenue && cuts.equals(other.cuts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(revenue, cuts);
    }

    @Override
    public String toString() {
        return "Revenue: " + revenue + ", Cuts: " + cuts;
    }
}
