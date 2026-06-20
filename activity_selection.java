import java.util.Arrays;
import java.util.Comparator;

public class ActivitySelection {

    public static int activitySelection(int[] start, int[] finish) {
        int n = start.length;
        if (n == 0) return 0;

        int[][] activities = new int[n][2];
        for (int i = 0; i < n; i++) {
            activities[i][0] = start[i];
            activities[i][1] = finish[i];
        }

        Arrays.sort(activities, Comparator.comparingInt(a -> a[1]));

        int count = 1;
        int lastSelected = 0;

        for (int i = 1; i < n; i++) {
            if (activities[i][0] > activities[lastSelected][1]) {
                count++;
                lastSelected = i;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        int[] start = {1, 3, 0, 5, 8, 5};
        int[] finish = {2, 4, 6, 7, 9, 9};

        System.out.println(activitySelection(start, finish));
    }
}
