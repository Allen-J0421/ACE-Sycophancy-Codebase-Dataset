import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
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
        return selectMaximumNonOverlappingActivities(startTimes, finishTimes).count();
    }

    public static SelectionResult selectMaximumNonOverlappingActivities(
        int[] startTimes,
        int[] finishTimes
    ) {
        Activity[] activities = buildActivities(startTimes, finishTimes);
        if (activities.length == 0) {
            return new SelectionResult(0, List.of());
        }
        return selectActivities(sortByFinishTime(activities));
    }

    public static void main(String[] args) {
        int[] startTimes = {1, 3, 0, 5, 8, 5};
        int[] finishTimes = {2, 4, 6, 7, 9, 9};

        SelectionResult result = selectMaximumNonOverlappingActivities(startTimes, finishTimes);
        System.out.println(result.count());

        if (result.count() != 4 || result.selectedActivities().size() != 4) {
            throw new IllegalStateException("Unexpected sample result: " + result);
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
            if (finishTimes[i] < startTimes[i]) {
                throw new IllegalArgumentException(
                    "finish time cannot be earlier than start time at index " + i
                );
            }
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

    private static SelectionResult selectActivities(Activity[] sortedActivities) {
        List<ActivityInterval> selectedActivities = new ArrayList<>();
        selectedActivities.add(toActivityInterval(sortedActivities[0]));
        int lastSelectedFinish = sortedActivities[0].finishTime();

        for (int i = 1; i < sortedActivities.length; i++) {
            Activity candidate = sortedActivities[i];
            if (candidate.startTime() >= lastSelectedFinish) {
                selectedActivities.add(toActivityInterval(candidate));
                lastSelectedFinish = candidate.finishTime();
            }
        }

        return new SelectionResult(selectedActivities.size(), List.copyOf(selectedActivities));
    }

    private record Activity(int startTime, int finishTime) {}

    private static ActivityInterval toActivityInterval(Activity activity) {
        return new ActivityInterval(activity.startTime(), activity.finishTime());
    }

    public record ActivityInterval(int startTime, int finishTime) {}

    public record SelectionResult(int count, List<ActivityInterval> selectedActivities) {
        public SelectionResult {
            Objects.requireNonNull(selectedActivities, "selectedActivities");
            selectedActivities = List.copyOf(selectedActivities);
        }
    }
}
