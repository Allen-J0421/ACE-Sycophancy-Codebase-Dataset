class TabularMstFormatter implements MstFormatter {

    @Override
    public String format(MstResult result) {
        StringBuilder sb = new StringBuilder();
        sb.append("Edge \tWeight\n");
        for (Edge edge : result.edges()) {
            sb.append(edge).append("\n");
        }
        sb.append("Total weight: ").append(result.totalWeight());
        return sb.toString();
    }
}
