import java.util.ArrayList;

final class ArticulationPointAnalysis implements GraphAnalysis<ArrayList<Integer>> {
    @Override
    public ArrayList<Integer> analyze(Graph graph) {
        return new ArticulationPointTraversal(graph).run();
    }

    private static final class ArticulationPointTraversal extends DepthFirstGraphAnalysis<ArrayList<Integer>> {
        private static final int NO_ARTICULATION_POINTS = -1;

        private final int[] lowLink;
        private final boolean[] articulationPoint;

        private ArticulationPointTraversal(Graph graph) {
            super(graph);
            this.lowLink = new int[graph.vertexCount()];
            this.articulationPoint = new boolean[graph.vertexCount()];
        }

        @Override
        protected void onDiscoverVertex(int vertex, int parent) {
            lowLink[vertex] = discoveryTimeOf(vertex);
        }

        @Override
        protected void afterChildTraversal(int vertex, int child, int parent) {
            lowLink[vertex] = Math.min(lowLink[vertex], lowLink[child]);

            if (parent != ROOT_PARENT && lowLink[child] >= discoveryTimeOf(vertex)) {
                articulationPoint[vertex] = true;
            }
        }

        @Override
        protected void onBackEdge(int vertex, int neighbor) {
            lowLink[vertex] = Math.min(lowLink[vertex], discoveryTimeOf(neighbor));
        }

        @Override
        protected void onCompleteVertex(int vertex, int parent, int childCount) {
            if (parent == ROOT_PARENT && childCount > 1) {
                articulationPoint[vertex] = true;
            }
        }

        @Override
        protected ArrayList<Integer> buildResult() {
            ArrayList<Integer> result = new ArrayList<>();

            for (int vertex = 0; vertex < articulationPoint.length; vertex++) {
                if (articulationPoint[vertex]) {
                    result.add(vertex);
                }
            }

            if (result.isEmpty()) {
                result.add(NO_ARTICULATION_POINTS);
            }

            return result;
        }
    }
}
