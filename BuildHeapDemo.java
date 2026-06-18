public final class BuildHeapDemo {

    private BuildHeapDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        try {
            int[] values = HeapDemoInputParser.parse(args);
            HeapBuildResult result = BuildHeap.analyze(values);
            System.out.println(HeapBuildResultFormatter.format(result));
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            System.err.println(HeapDemoInputParser.usage());
        }
    }
}
