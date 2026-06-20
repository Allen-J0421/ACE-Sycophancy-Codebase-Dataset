import java.util.List;

public final class ComponentFormatter {

    private ComponentFormatter() {
    }

    public static String formatComponents(List<? extends List<Integer>> components) {
        StringBuilder output = new StringBuilder();
        for (List<Integer> component : components) {
            if (output.length() > 0) {
                output.append(System.lineSeparator());
            }
            output.append(formatComponent(component));
        }
        return output.toString();
    }

    public static String formatComponent(List<Integer> component) {
        StringBuilder line = new StringBuilder();
        for (int vertex : component) {
            if (line.length() > 0) {
                line.append(' ');
            }
            line.append(vertex);
        }
        return line.toString();
    }
}
