final class ResidualNetwork {
    private final int[][] residualCapacities;

    ResidualNetwork(int[][] capacities) {
        this.residualCapacities = copyMatrix(capacities);
    }

    int size() {
        return residualCapacities.length;
    }

    int residualCapacity(int from, int to) {
        return residualCapacities[from][to];
    }

    boolean hasResidualCapacity(int from, int to) {
        return residualCapacities[from][to] > 0;
    }

    int bottleneckCapacity(int[] parent, int source, int sink) {
        int pathFlow = Integer.MAX_VALUE;

        for (int vertex = sink; vertex != source; vertex = parent[vertex]) {
            int predecessor = parent[vertex];
            pathFlow = Math.min(pathFlow, residualCapacities[predecessor][vertex]);
        }

        return pathFlow;
    }

    void augmentPath(int[] parent, int source, int sink, int flow) {
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
