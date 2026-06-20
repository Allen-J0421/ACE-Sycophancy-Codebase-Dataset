public final class ConnectedComponents {

    private ConnectedComponents() {
    }

    public static void main(String[] args) {
        ConnectedComponentsResult result =
                GraphComponentFinder.findConnectedComponents(GraphExamples.createSampleGraph());
        System.out.println(result.format());
    }
}
