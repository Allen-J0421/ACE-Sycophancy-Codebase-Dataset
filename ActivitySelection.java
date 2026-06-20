import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public final class ActivitySelection {

    private ActivitySelection() {
        // Utility class.
    }

    /**
     * Backward-compatible entry point retained for callers that use the original name.
     */
    public static int activitySelection(int[] startTimes, int[] finishTimes) {
        return maximumNonOverlappingActivities(startTimes, finishTimes);
    }

    public static int maximumNonOverlappingActivities(int[] startTimes, int[] finishTimes) {
        Activity[] activities = buildActivities(startTimes, finishTimes);
        if (activities.length == 0) {
            return 0;
        }
        return countCompatibleActivities(sortByFinishTime(activities));
    }

    public static void main(String[] args) {
        int[] startTimes = {1, 3, 0, 5, 8, 5};
        int[] finishTimes = {2, 4, 6, 7, 9, 9};

        int selectedCount = maximumNonOverlappingActivities(startTimes, finishTimes);
        System.out.println(selectedCount);

        if (selectedCount != 4) {
            throw new IllegalStateException("Unexpected sample result: " + selectedCount);
        }
    }

    private static Activity[] buildActivities(int[] startTimes, int[] finishTimes) {
        Objects.requireNonNull(startTimes, "startTimes");
        Objects.requireNonNull(finishTimes, "finishTimes");

        if (startTimes.length != finishTimes.length) {
            throw new IllegalArgumentException("startTimes and finishTimes must have the same length");
        }

        Activity[] activities = new Activity[startTimes.length];
        for (int i = 0; i < startTimes.length; i++) {
            activities[i] = new Activity(startTimes[i], finishTimes[i]);
        }
        return activities;
    }

    private static Activity[] sortByFinishTime(Activity[] activities) {
        Activity[] sortedActivities = Arrays.copyOf(activities, activities.length);
        Arrays.sort(
            sortedActivities,
            Comparator.comparingInt(Activity::finishTime).thenComparingInt(Activity::startTime)
        );
        return sortedActivities;
    }

    private static int countCompatibleActivities(Activity[] sortedActivities) {
        int selectedCount = 1;
        int lastSelectedFinish = sortedActivities[0].finishTime();

        for (int i = 1; i < sortedActivities.length; i++) {
            Activity candidate = sortedActivities[i];
            if (candidate.startTime() >= lastSelectedFinish) {
                selectedCount++;
                lastSelectedFinish = candidate.finishTime();
            }
        }

        return selectedCount;
    }

    private record Activity(int startTime, int finishTime) {}
}
