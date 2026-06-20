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

    public int bottleneckCapacity(int[] parent, int source, int sink) {
        int pathFlow = Integer.MAX_VALUE;

        for (int vertex = sink; vertex != source; vertex = parent[vertex]) {
            int predecessor = parent[vertex];
            pathFlow = Math.min(pathFlow, residualCapacities[predecessor][vertex]);
        }

        return pathFlow;
    }

    public void augmentPath(int[] parent, int source, int sink, int flow) {
        for (int vertex = sink; vertex != source; vertex = parent[vertex]) {
            int predecessor = parent[vertex];
            residualCapacities[predecessor][vertex] -= flow;
            residualCapacities[vertex][predecessor] += flow;
        }
    }

    private static int[][] copyMatrix(int[][] sourceMatrix) {
        int[][] copy = new int[sourceMatrix.length][sourceMatrix.length];
        for (int row = 0; row < sourceMatrix.length; row++) {
            System.arraycopy(sourceMatrix[row], 0, copy[row], 0, sourceMatrix[row].length);
        }
        return copy;
    }
}
