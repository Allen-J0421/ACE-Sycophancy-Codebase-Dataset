public final class QuickSelectDemo {

    private QuickSelectDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        int[] values = { 10, 4, 5, 8, 6, 11, 26 };
        int rank = 3;

        System.out.println(
            "K-th smallest element in array: "
            + QuickSelect.selectKthSmallest(values, rank));
    }
}
