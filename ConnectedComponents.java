import java.util.List;

public final class ConnectedComponents {

    private ConnectedComponents() {
    }

    public static void main(String[] args) {
        List<List<Integer>> components =
                GraphComponentFinder.findConnectedComponents(GraphExamples.createSampleGraph());
        System.out.println(ComponentFormatter.format(components));
    }
}
