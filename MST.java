public final class MST {
    private MST() {
    }

    public static void main(String[] args) {
        MstResult result = PrimsMinimumSpanningTree.compute(SampleGraphs.fiveVertexExample());
        System.out.println(MstFormatter.format(result));
    }
}
