public final class TwoPointersTechniqueDemo {

    private TwoPointersTechniqueDemo() {
        // Demo entry point only.
    }

    public static void main(String[] args) {
        printResult("unsorted", TwoPointersTechnique.hasPairWithSum(
            TwoPointersTechniqueSamples.unsortedSample(),
            TwoPointersTechniqueSamples.target()
        ));
        printResult("sorted", TwoPointersTechnique.hasPairWithSumSorted(
            TwoPointersTechniqueSamples.sortedSample(),
            TwoPointersTechniqueSamples.target()
        ));
    }

    private static void printResult(String label, boolean result) {
        System.out.println(label + ": " + result);
    }
}
