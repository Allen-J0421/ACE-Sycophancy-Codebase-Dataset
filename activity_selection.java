import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class ActivitySelection {

    private record Activity(int start, int finish) {}

    public static int activitySelection(int[] start, int[] finish) {
        if (start.length != finish.length) {
            throw new IllegalArgumentException("start and finish arrays must have equal length");
        }
        return countSelected(IntStream.range(0, start.length)
            .mapToObj(i -> new Activity(start[i], finish[i]))
            .sorted(Comparator.comparingInt(Activity::finish))
            .toList());
    }

    private static int countSelected(List<Activity> sortedByFinish) {
        int count = 0;
        int lastFinish = Integer.MIN_VALUE;

        for (Activity activity : sortedByFinish) {
            if (activity.start() > lastFinish) {
                count++;
                lastFinish = activity.finish();
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
