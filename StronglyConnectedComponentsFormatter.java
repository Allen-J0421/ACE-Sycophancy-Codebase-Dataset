import java.util.stream.Collectors;

public class StronglyConnectedComponentsFormatter {

    private StronglyConnectedComponentsFormatter() {}

    public static String format(StronglyConnectedComponentsResult result) {
        StringBuilder sb = new StringBuilder("Strongly Connected Components:\n");
        for (StronglyConnectedComponent scc : result) {
            sb.append(scc.getVertices().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(" ")));
            sb.append('\n');
        }
        return sb.toString().stripTrailing();
    }
}
