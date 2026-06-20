import java.util.ArrayList;
import java.util.List;

final class ArticulationPointAnalysis implements GraphAnalysis<List<Integer>> {
    @Override
    public List<Integer> analyze(Graph graph) {
        validateGraph(graph);
        return new ArticulationPointTraversal(graph).run();
    }

    private static void validateGraph(Graph graph) {
        if (!graph.isUndirected()) {
            throw new IllegalArgumentException("Articulation points require an undirected graph");
        }
    }

    private static final class ArticulationPointTraversal extends DepthFirstGraphAnalysis<List<Integer>> {
        private static final int NO_ARTICULATION_POINTS = -1;

        private final int[] lowLink;
        private final boolean[] articulationPoint;

        private ArticulationPointTraversal(Graph graph) {
            super(graph);
            this.lowLink = new int[vertexCount()];
            this.articulationPoint = new boolean[vertexCount()];
        }

        @Override
        protected void onDiscoverVertex(int vertex, int parent) {
            lowLink[vertex] = discoveryTimeOf(vertex);
        }

        @Override
        protected void afterChildTraversal(int vertex, int child, int parent) {
            lowLink[vertex] = Math.min(lowLink[vertex], lowLink[child]);

            if (!isRoot(parent) && lowLink[child] >= discoveryTimeOf(vertex)) {
                articulationPoint[vertex] = true;
            }
        }

        @Override
        protected void onBackEdge(int vertex, int neighbor) {
            lowLink[vertex] = Math.min(lowLink[vertex], discoveryTimeOf(neighbor));
        }

        @Override
        protected void onCompleteVertex(int vertex, int parent, int childCount) {
            if (isRoot(parent) && childCount > 1) {
                articulationPoint[vertex] = true;
            }
        }

        @Override
        protected List<Integer> buildResult() {
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
