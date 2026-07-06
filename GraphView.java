import java.util.List;

class GraphView implements PartitionVisitor {

    @Override
    public void visitBipartite(List<Integer> setA, List<Integer> setB) {
        System.out.println(true);
    }

    @Override
    public void visitNonBipartite() {
        System.out.println(false);
    }
}
