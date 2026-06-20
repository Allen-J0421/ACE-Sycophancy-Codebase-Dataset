public final class FloydWarshallDemo {
    private FloydWarshallDemo() {
    }

    public static void main(String[] args) {
        int[][] shortestPaths = FloydWarshall.shortestPaths(ExampleGraphs.weightedDirectedGraph());
        System.out.print(DistanceMatrixFormatter.format(shortestPaths));
    }
}
