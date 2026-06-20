final class ComponentFormatter {

    private ComponentFormatter() {
    }

    static String format(ConnectedComponentsResult result) {
        StringBuilder output = new StringBuilder();

        for (int componentIndex = 0; componentIndex < result.componentCount(); componentIndex++) {
            if (componentIndex > 0) {
                output.append(System.lineSeparator());
            }

            appendComponent(output, result.componentAt(componentIndex));
        }

        return output.toString();
    }

    static String format(ConnectedComponent component) {
        StringBuilder output = new StringBuilder();
        appendComponent(output, component);
        return output.toString();
    }

    private static void appendComponent(StringBuilder output, ConnectedComponent component) {
        for (int vertexIndex = 0; vertexIndex < component.size(); vertexIndex++) {
            if (vertexIndex > 0) {
                output.append(' ');
            }

            output.append(component.vertices().get(vertexIndex).index());
        }
    }
}
