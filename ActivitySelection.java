import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class ActivitySelection {
    private static final Comparator<Activity> BY_FINISH_TIME =
        Comparator.comparingInt(Activity::finishTime);

    private ActivitySelection() {
    }

    public static int activitySelection(int[] start, int[] finish) {
        return countSelectedActivities(sortByFinishTime(buildActivities(start, finish)));
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

    private static Activity[] sortByFinishTime(Activity[] activities) {
        Arrays.sort(activities, BY_FINISH_TIME);
        return activities;
    }

    private static int countSelectedActivities(Activity[] activities) {
        if (activities.length == 0) {
            return 0;
        }

        int count = 1;
        Activity lastSelected = activities[0];

        // Greedily keep the earliest-finishing compatible activity.
        for (int i = 1; i < activities.length; i++) {
            Activity current = activities[i];
            if (current.canFollow(lastSelected)) {
                count++;
                lastSelected = current;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        int[] start = {1, 3, 0, 5, 8, 5};
        int[] finish = {2, 4, 6, 7, 9, 9};

        System.out.println(activitySelection(start, finish));
    }

    private static final class Activity {
        private final int startTime;
        private final int finishTime;

        private Activity(int startTime, int finishTime) {
            this.startTime = startTime;
            this.finishTime = finishTime;
        }

        private int finishTime() {
            return finishTime;
        }

        private boolean canFollow(Activity other) {
            return startTime > other.finishTime;
        }
    }
}
