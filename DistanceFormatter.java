import java.util.ArrayList;
import java.util.List;

final class DistanceFormatter {
    private DistanceFormatter() {
    }

    static String format(List<Integer> distances) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < distances.size(); i++) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(distances.get(i));
        }
        return builder.toString();
    }

    static String format(int... distances) {
        List<Integer> boxed = new ArrayList<>(distances.length);
        for (int distance : distances) {
            boxed.add(distance);
        }
        return format(boxed);
    }
}
