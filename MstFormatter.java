final class MstFormatter {
    private MstFormatter() {
    }

    static String format(MstResult result) {
        StringBuilder output = new StringBuilder("Edge \tWeight");
        for (Edge edge : result.edges()) {
            output.append(System.lineSeparator())
                    .append(edge.from())
                    .append(" - ")
                    .append(edge.to())
                    .append('\t')
                    .append(edge.weight());
        }
        return output.toString();
    }
}
