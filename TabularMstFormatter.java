/**
 * Formats a minimum spanning tree as a two-column {@code Edge \t Weight} table,
 * matching the classic adjacency-matrix Prim's output.
 */
public final class TabularMstFormatter implements MstFormatter {

    @Override
    public String format(MstResult result) {
        StringBuilder table = new StringBuilder("Edge \tWeight");
        for (Edge edge : result.edges()) {
            table.append('\n')
                 .append(edge.source())
                 .append(" - ")
                 .append(edge.destination())
                 .append('\t')
                 .append(edge.weight());
        }
        return table.toString();
    }
}
