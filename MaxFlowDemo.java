final class MaxFlowDemo {
    private static final int SOURCE = 0;
    private static final int SINK = 5;

    private MaxFlowDemo() {
    }

    public static void main(String[] args) {
        System.out.println("The maximum possible flow is "
                           + new MaxFlow().fordFulkerson(sampleGraph(), SOURCE, SINK));
    }

    private static int[][] sampleGraph() {
        return new int[][] {
            { 0, 16, 13, 0, 0, 0 },
            { 0, 0, 10, 12, 0, 0 },
            { 0, 4, 0, 0, 14, 0 },
            { 0, 0, 9, 0, 0, 20 },
            { 0, 0, 0, 7, 0, 4 },
            { 0, 0, 0, 0, 0, 0 }
        };
    }
}
