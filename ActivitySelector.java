import java.util.Arrays;
import java.util.Comparator;

final class ActivitySelector {
    private static final Comparator<Activity> BY_FINISH_TIME =
        Comparator.comparingInt(Activity::getFinishTime);

    private ActivitySelector() {
    }

    static int maxCompatibleActivities(int[] startTimes, int[] finishTimes) {
        validateSchedule(startTimes, finishTimes);

        if (startTimes.length == 0) {
            return 0;
        }

        Activity[] activities = buildActivities(startTimes, finishTimes);
        Arrays.sort(activities, BY_FINISH_TIME);

        return countCompatibleActivities(activities);
    }

    private static void validateSchedule(int[] startTimes, int[] finishTimes) {
        if (startTimes == null || finishTimes == null) {
            throw new IllegalArgumentException("Start and finish times must not be null.");
        }

        if (startTimes.length != finishTimes.length) {
            throw new IllegalArgumentException("Start and finish times must have the same length.");
        }
    }

    private static Activity[] buildActivities(int[] startTimes, int[] finishTimes) {
        Activity[] activities = new Activity[startTimes.length];

        for (int i = 0; i < startTimes.length; i++) {
            activities[i] = new Activity(startTimes[i], finishTimes[i]);
        }

        return activities;
    }

    private static int countCompatibleActivities(Activity[] activities) {
        int selectedCount = 1;
        Activity lastSelected = activities[0];

        for (int i = 1; i < activities.length; i++) {
            Activity candidate = activities[i];

            if (candidate.getStartTime() > lastSelected.getFinishTime()) {
                selectedCount++;
                lastSelected = candidate;
            }
        }

        return selectedCount;
    }
}
