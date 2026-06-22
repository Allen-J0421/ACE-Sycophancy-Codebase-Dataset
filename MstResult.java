import java.util.List;

record MstResult(List<Edge> edges) {
    MstResult {
        edges = List.copyOf(edges);
    }
}
