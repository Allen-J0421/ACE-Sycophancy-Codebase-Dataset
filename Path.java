import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

class Path {
    private final Deque<Integer> nodes;
    private final int totalDistance;

    private Path(Deque<Integer> nodes, int totalDistance) {
        this.nodes = new LinkedList<>(nodes);
        this.totalDistance = totalDistance;
    }

    static Path of(Deque<Integer> nodes, int totalDistance) {
        return new Path(nodes, totalDistance);
    }

    List<Integer> getNodes() {
        return Collections.unmodifiableList(new ArrayList<>(nodes));
    }

    int getTotalDistance() {
        return totalDistance;
    }

    int getLength() {
        return nodes.size();
    }

    boolean contains(int node) {
        return nodes.contains(node);
    }

    @Override
    public String toString() {
        return String.format("Path(nodes=%s, distance=%d)", nodes, totalDistance);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Path)) return false;
        Path other = (Path) obj;
        return nodes.equals(other.nodes) && totalDistance == other.totalDistance;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, totalDistance);
    }
}
