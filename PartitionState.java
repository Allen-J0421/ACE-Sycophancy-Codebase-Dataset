import java.util.ArrayList;
import java.util.List;

class PartitionState {

    enum Color {
        RED, BLUE;

        Color opposite() {
            return this == RED ? BLUE : RED;
        }
    }

    private final Color[] colors;

    PartitionState(int V) {
        colors = new Color[V];
    }

    void setColor(int v, Color c) {
        colors[v] = c;
    }

    Color getColor(int v) {
        return colors[v];
    }

    boolean isUncolored(int v) {
        return colors[v] == null;
    }

    Partition buildPartition() {
        List<Integer> setA = new ArrayList<>();
        List<Integer> setB = new ArrayList<>();
        for (int i = 0; i < colors.length; i++) {
            if (colors[i] == Color.RED) {
                setA.add(i);
            } else {
                setB.add(i);
            }
        }
        return new Partition(setA, setB);
    }
}
