import java.util.List;

public final class Main {
    private static final int SAMPLE_SOURCE = 0;

    private Main() {
    }

    public static void main(String[] args) {
        List<Integer> result = ShortestPath.shortestPaths(
            GraphFixtures.weightedUndirectedExample(),
            SAMPLE_SOURCE
        );
        System.out.println(DistanceFormatter.format(result));
    }
}
