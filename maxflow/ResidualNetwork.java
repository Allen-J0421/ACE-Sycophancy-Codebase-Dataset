package maxflow;

public final class ResidualNetwork {
    private final int[][] residualCapacities;

    ResidualNetwork(int[][] capacities) {
        this.residualCapacities = copyMatrix(capacities);
    }

    public int vertexCount() {
        return residualCapacities.length;
    }

    public int residualCapacity(int from, int to) {
        return residualCapacities[from][to];
    }

    public boolean hasResidualCapacity(int from, int to) {
        return residualCapacities[from][to] > 0;
    }

    public int bottleneckCapacity(AugmentingPath path) {
        int pathFlow = Integer.MAX_VALUE;

        for (int vertex = path.sink(); vertex != path.source(); vertex = path.parentOf(vertex)) {
            int predecessor = path.parentOf(vertex);
            pathFlow = Math.min(pathFlow, residualCapacities[predecessor][vertex]);
        }

        return pathFlow;
    }

    public int augmentPath(AugmentingPath path) {
        int flow = bottleneckCapacity(path);
        for (int vertex = path.sink(); vertex != path.source(); vertex = path.parentOf(vertex)) {
            int predecessor = path.parentOf(vertex);
            residualCapacities[predecessor][vertex] -= flow;
            residualCapacities[vertex][predecessor] += flow;
        }

        return flow;
    }

    private static int[][] copyMatrix(int[][] sourceMatrix) {
        int[][] copy = new int[sourceMatrix.length][sourceMatrix.length];
        for (int row = 0; row < sourceMatrix.length; row++) {
            System.arraycopy(sourceMatrix[row], 0, copy[row], 0, sourceMatrix[row].length);
        }
        return copy;
    }
}
