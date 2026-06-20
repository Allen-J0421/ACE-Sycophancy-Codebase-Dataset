public class StronglyConnectedComponentsFormatter {

    public String format(StronglyConnectedComponentsResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Strongly Connected Components:\n");
        for (StronglyConnectedComponent scc : result.getComponents()) {
            for (int vertex : scc.getVertices()) {
                sb.append(vertex).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString().stripTrailing();
    }
}
