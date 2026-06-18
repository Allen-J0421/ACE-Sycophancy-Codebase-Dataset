import java.util.Arrays;
import java.util.Comparator;

public class ActivitySelection {

        public static int activitySelection(int[] start, int[] finish) {
        int n = start.length;
        int[][] arr = new int[n][2];
        for (int i = 0; i < n; i++) {
            arr[i][0] = start[i];
            arr[i][1] = finish[i];
        }

        Arrays.sort(arr, Comparator.comparingInt(a -> a[1]));

        int count = 1;

        int j = 0;

        for (int i = 1; i < n; i++) {

            if (arr[i][0] > arr[j][1]) {
                count++;

                j = i;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        int[] start = {1, 3, 0, 5, 8, 5};
        int[] finish = {2, 4, 6, 7, 9, 9};

        System.out.println(
            activitySelection(start, finish));
    }
}
