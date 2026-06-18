import java.util.List;

public final class Main {
    private static final int SAMPLE_SOURCE = 0;

    private Main() {
    }

    public static void main(String[] args) {
        List<Integer> result = ShortestPath.shortestPaths(
            SampleGraphs.weightedUndirectedExample(),
            SAMPLE_SOURCE
        );
        System.out.println(formatDistances(result));
    }

    private static String formatDistances(List<Integer> distances) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < distances.size(); i++) {
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(distances.get(i));
        }
        return builder.toString();
    }
}
