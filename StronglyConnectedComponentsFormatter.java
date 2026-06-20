public final class StronglyConnectedComponentsFormatter {
    private static final String HEADER = "Strongly Connected Components:";

    public String format(StronglyConnectedComponentsResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Result must not be null.");
        }

        StringBuilder output = new StringBuilder(HEADER).append('\n');
        for (StronglyConnectedComponent component : result.components()) {
            output.append(format(component)).append('\n');
        }
        return output.toString();
    }

    public String format(StronglyConnectedComponent component) {
        if (component == null) {
            throw new IllegalArgumentException("Component must not be null.");
        }

        StringBuilder output = new StringBuilder();
        for (Vertex vertex : component.vertices()) {
            output.append(vertex.id()).append(' ');
        }
        return output.toString();
    }
}
