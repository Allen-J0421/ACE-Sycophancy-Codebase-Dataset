import java.util.ArrayList;
import java.util.Arrays;

final class Distances {
    private final int[] values;

    private Distances(int[] values) {
        this.values = values;
    }

    static Distances forSource(int vertexCount, int source) {
        if (source < 0 || source >= vertexCount) {
            throw new IndexOutOfBoundsException("source out of range: " + source);
        }

        int[] values = new int[vertexCount];
        Arrays.fill(values, Integer.MAX_VALUE);
        values[source] = 0;
        return new Distances(values);
    }

    boolean isStale(QueueEntry entry) {
        return entry.distance() > values[entry.vertex()];
    }

    boolean tryUpdate(int vertex, int distance) {
        if (distance >= values[vertex]) {
            return false;
        }

        values[vertex] = distance;
        return true;
    }

    ArrayList<Integer> toList() {
        ArrayList<Integer> result = new ArrayList<>(values.length);
        for (int value : values) {
            result.add(value);
        }
        return result;
    }
}
