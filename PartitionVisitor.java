import java.util.List;

interface PartitionVisitor {
    void visitBipartite(List<Integer> setA, List<Integer> setB);
    void visitNonBipartite();
}
