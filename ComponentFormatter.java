final class ComponentFormatter {

    private ComponentFormatter() {
    }

    static String format(ConnectedComponentsResult result) {
        StringBuilder output = new StringBuilder();
        boolean isFirstComponent = true;

        for (ConnectedComponent component : result) {
            if (!isFirstComponent) {
                output.append(System.lineSeparator());
            }

            appendComponent(output, component);
            isFirstComponent = false;
        }

        return output.toString();
    }

    static String format(ConnectedComponent component) {
        StringBuilder output = new StringBuilder();
        appendComponent(output, component);
        return output.toString();
    }

    private static void appendComponent(StringBuilder output, ConnectedComponent component) {
        boolean isFirstVertex = true;

        for (Vertex vertex : component) {
            if (!isFirstVertex) {
                output.append(' ');
            }

            output.append(vertex.index());
            isFirstVertex = false;
        }
    }
}
