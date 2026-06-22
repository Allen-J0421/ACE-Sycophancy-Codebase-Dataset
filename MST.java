public final class MST {
    private MST() {
    }

    public static void main(String[] args) {
        MstApplication application = new MstApplication(
                new SampleAdjacencyMatrixSource(),
                new NeighborListGraphFactory(),
                new PrimsMinimumSpanningTree(),
                new TabularMstFormatter());
        System.out.println(application.run());
    }
}
