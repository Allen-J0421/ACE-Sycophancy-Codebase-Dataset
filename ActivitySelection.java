import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class ActivitySelection {
    private ActivitySelection() {
    }

    public static int activitySelection(int[] start, int[] finish) {
        Activity[] activities = buildActivities(start, finish);
        if (activities.length == 0) {
            return 0;
        }

        Arrays.sort(activities, Comparator.comparingInt(Activity::finish));

        int count = 1;
        Activity lastSelected = activities[0];

        for (int i = 1; i < activities.length; i++) {
            Activity current = activities[i];
            if (current.start() > lastSelected.finish()) {
                count++;
                lastSelected = current;
            }
        }

        return count;
    }

    private static Activity[] buildActivities(int[] start, int[] finish) {
        Objects.requireNonNull(start, "start must not be null");
        Objects.requireNonNull(finish, "finish must not be null");

        if (start.length != finish.length) {
            throw new IllegalArgumentException("start and finish must have the same length");
        }

        Activity[] activities = new Activity[start.length];
        for (int i = 0; i < start.length; i++) {
            activities[i] = new Activity(start[i], finish[i]);
        }
        return activities;
    }

    public static void main(String[] args) {
        int[] start = {1, 3, 0, 5, 8, 5};
        int[] finish = {2, 4, 6, 7, 9, 9};

        System.out.println(activitySelection(start, finish));
    }

    private record Activity(int start, int finish) {
    }
}
