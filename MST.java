public final class MST {
    private MST() {
    }

    public static void main(String[] args) {
        MinimumSpanningTreeAlgorithm algorithm = new PrimsMinimumSpanningTree();
        MstResult result = algorithm.compute(SampleGraphs.fiveVertexExample());
        System.out.println(MstFormatter.format(result));
    }
}
