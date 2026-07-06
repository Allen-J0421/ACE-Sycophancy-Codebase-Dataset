import java.util.ArrayList;
import java.util.List;

class Partition {
    final List<Integer> setA = new ArrayList<>();
    final List<Integer> setB = new ArrayList<>();
    final boolean bipartite;

    Partition(boolean bipartite) {
        this.bipartite = bipartite;
    }

    void accept(PartitionVisitor visitor) {
        if (bipartite) {
            visitor.visitBipartite(setA, setB);
        } else {
            visitor.visitNonBipartite();
        }
    }
}
