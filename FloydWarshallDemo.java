import java.util.List;
import java.util.stream.Collectors;

public class FloydWarshallDemo {

    public static void main(String[] args) {
        final int INF = FloydWarshall.INF;
        int[][] graph = {
            {0,   4,   INF, 5,   INF},
            {INF, 0,   1,   INF, 6  },
            {2,   INF, 0,   3,   INF},
            {INF, INF, 1,   0,   2  },
            {1,   INF, INF, 4,   0  }
        };

        FloydWarshall.Result result = FloydWarshall.compute(graph);

        System.out.println("Shortest distance matrix:");
        printDistMatrix(result);

        System.out.println("\nShortest paths:");
        int n = result.vertexCount();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    printPath(result, i, j);
                }
            }
        }
    }

    private static void printDistMatrix(FloydWarshall.Result result) {
        int n = result.vertexCount();
        String[][] cells = new String[n][n];
        int colWidth = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int d = result.getDistance(i, j);
                cells[i][j] = d >= FloydWarshall.INF ? "INF" : String.valueOf(d);
                colWidth = Math.max(colWidth, cells[i][j].length());
            }
        }
        String fmt = "%-" + (colWidth + 1) + "s";
        for (String[] row : cells) {
            for (String cell : row) {
                System.out.printf(fmt, cell);
            }
            System.out.println();
        }
    }

    private static void printPath(FloydWarshall.Result result, int from, int to) {
        List<Integer> path = result.getPath(from, to);
        if (path.isEmpty()) {
            System.out.printf("%d -> %d: unreachable%n", from, to);
        } else {
            String pathStr = path.stream().map(String::valueOf).collect(Collectors.joining(" -> "));
            System.out.printf("%d -> %d (dist=%d): %s%n",
                from, to, result.getDistance(from, to), pathStr);
        }
    }
}
