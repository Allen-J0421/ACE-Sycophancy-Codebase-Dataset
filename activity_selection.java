import java.util.Comparator;
import java.util.stream.IntStream;

public class ActivitySelection {

    private record Activity(int start, int finish) {}

    public static int activitySelection(int[] start, int[] finish) {
        if (start.length != finish.length) {
            throw new IllegalArgumentException("start and finish arrays must have equal length");
        }
        int n = start.length;
        if (n == 0) return 0;

        Activity[] activities = IntStream.range(0, n)
            .mapToObj(i -> new Activity(start[i], finish[i]))
            .sorted(Comparator.comparingInt(Activity::finish))
            .toArray(Activity[]::new);

        return countSelected(activities);
    }

    private static int countSelected(Activity[] sortedByFinish) {
        int count = 1;
        int lastFinish = sortedByFinish[0].finish();

        for (int i = 1; i < sortedByFinish.length; i++) {
            if (sortedByFinish[i].start() > lastFinish) {
                count++;
                lastFinish = sortedByFinish[i].finish();
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
