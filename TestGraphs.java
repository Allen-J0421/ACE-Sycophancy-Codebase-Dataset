final class TestGraphs {
    private TestGraphs() {
    }

    static int[][] acyclicThreeNodeGraph() {
        return GraphFixtures.acyclicThreeNodeGraph();
    }

    static int[][] negativeCycleGraph() {
        return GraphFixtures.negativeCycleGraph();
    }

    static int[][] overflowGraph() {
        return GraphFixtures.overflowGraph();
    }

    static int[][] unreachableTwoNodeDistances() {
        return GraphFixtures.unreachableTwoNodeDistances();
    }

    static int[][] invalidRectangularMatrix() {
        return GraphFixtures.invalidRectangularMatrix();
    }

    static int[][] matrixWithNullRow() {
        return GraphFixtures.matrixWithNullRow();
    }

    static int[][] nonSquareRowMatrix() {
        return GraphFixtures.nonSquareRowMatrix();
    }
}
