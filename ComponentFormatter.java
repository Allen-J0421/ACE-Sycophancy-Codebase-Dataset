import java.util.List;

final class ComponentFormatter {

    private ComponentFormatter() {
    }

    static String format(List<List<Integer>> components) {
        StringBuilder output = new StringBuilder();

        for (int componentIndex = 0; componentIndex < components.size(); componentIndex++) {
            if (componentIndex > 0) {
                output.append(System.lineSeparator());
            }

            appendComponent(output, components.get(componentIndex));
        }

        return output.toString();
    }

    private static void appendComponent(StringBuilder output, List<Integer> component) {
        for (int vertex : component) {
            output.append(vertex).append(' ');
        }
    }
}
