/**
 * Entry point: computes the minimum spanning tree of the classic sample graph
 * using Prim's algorithm and prints it as a table.
 */
public final class MstApplication {

    private final MstAlgorithm algorithm;
    private final MstFormatter formatter;

    public MstApplication(MstAlgorithm algorithm, MstFormatter formatter) {
        this.algorithm = algorithm;
        this.formatter = formatter;
    }

    /** Computes and renders the spanning tree of the given graph. */
    public String run(WeightedGraph graph) {
        MstResult result = algorithm.computeMst(graph);
        return formatter.format(result);
    }

    public static void main(String[] args) {
        MstApplication app = new MstApplication(new PrimsMST(), new TabularMstFormatter());
        System.out.println(app.run(SampleGraphs.classicExample()));
    }
}
