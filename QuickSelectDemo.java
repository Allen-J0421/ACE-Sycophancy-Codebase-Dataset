public final class QuickSelectDemo {

    private QuickSelectDemo() {
        // Utility class.
    }

    public static void main(String[] args) {
        int[] values = { 10, 4, 5, 8, 6, 11, 26 };
        int kPosition = 3;

        try {
            int result = QuickSelect.selectKthSmallest(values, kPosition);
            System.out.println("K-th smallest element in array : " + result);
        } catch (IllegalArgumentException exception) {
            System.out.println("Index out of bound");
        }
    }
}
