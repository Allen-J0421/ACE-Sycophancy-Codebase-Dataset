import java.util.ArrayList;
import java.util.List;

final class ActivitySchedule {
    private final List<Activity> activities;

    private ActivitySchedule(List<Activity> activities) {
        this.activities = activities;
    }

    static ActivitySchedule from(int[] startTimes, int[] finishTimes) {
        validate(startTimes, finishTimes);

        List<Activity> activities = new ArrayList<>(startTimes.length);

        for (int i = 0; i < startTimes.length; i++) {
            activities.add(new Activity(startTimes[i], finishTimes[i]));
        }

        return new ActivitySchedule(activities);
    }

    boolean isEmpty() {
        return activities.isEmpty();
    }

    List<Activity> activitiesSortedByFinishTime() {
        List<Activity> sortedActivities = new ArrayList<>(activities);
        sortedActivities.sort(Activity.BY_FINISH_TIME);
        return sortedActivities;
    }

    private static void validate(int[] startTimes, int[] finishTimes) {
        if (startTimes == null || finishTimes == null) {
            throw new IllegalArgumentException("Start and finish times must not be null.");
        }

        if (startTimes.length != finishTimes.length) {
            throw new IllegalArgumentException("Start and finish times must have the same length.");
        }
    }
}
