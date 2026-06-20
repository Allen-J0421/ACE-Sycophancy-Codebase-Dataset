import java.util.Arrays;

final class BellmanFordDemo {
    private BellmanFordDemo() {
    }

    public static void main(String[] args) {
        int vertices = 5;
        int[][] edges = {
            {1, 3, 2},
            {4, 3, -1},
            {2, 4, 1},
            {1, 2, 1},
            {0, 1, 5}
        };
        int source = 0;

        int[] shortestDistances = BellmanFord.shortestPaths(vertices, edges, source);
        System.out.println(Arrays.toString(shortestDistances));
    }
}
