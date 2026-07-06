import java.util.Collections;
import java.util.List;

record Graph(List<List<Integer>> adj) {

    int vertexCount() {
        return adj.size();
    }

    List<Integer> neighbors(int v) {
        return Collections.unmodifiableList(adj.get(v));
    }
}
