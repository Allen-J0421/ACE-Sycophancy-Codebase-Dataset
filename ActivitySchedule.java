import java.util.Arrays;

final class ActivitySchedule {
    private final Activity[] activities;

    private ActivitySchedule(Activity[] activities) {
        this.activities = activities;
    }

    static ActivitySchedule from(int[] startTimes, int[] finishTimes) {
        validate(startTimes, finishTimes);

        Activity[] activities = new Activity[startTimes.length];

        for (int i = 0; i < startTimes.length; i++) {
            activities[i] = new Activity(startTimes[i], finishTimes[i]);
        }

        return new ActivitySchedule(activities);
    }

    boolean isEmpty() {
        return activities.length == 0;
    }

    Activity[] activitiesByFinishTime() {
        Activity[] sortedActivities = Arrays.copyOf(activities, activities.length);
        Arrays.sort(sortedActivities, Activity.BY_FINISH_TIME);
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
