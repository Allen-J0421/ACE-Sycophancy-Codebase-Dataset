public class QuickSortDemo {

    public static void main(String[] args) {
        int[] arr = {10, 7, 8, 9, 1, 5};

        new QuickSort().sort(arr);

        for (int val : arr) {
            System.out.print(val + " ");
        }
    }
}
