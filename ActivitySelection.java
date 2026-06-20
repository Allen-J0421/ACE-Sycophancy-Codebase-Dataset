import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class ActivitySelection {

    private static final Comparator<ActivityInterval> BY_FINISH_THEN_START =
        Comparator.comparingInt(ActivityInterval::finishTime)
            .thenComparingInt(ActivityInterval::startTime);

    private ActivitySelection() {
        // Utility class.
    }

    /**
     * Selects the maximum set of non-overlapping activities using the greedy
     * earliest-finish-time strategy.
     */
    public static SelectionResult selectMaximumNonOverlappingActivities(
        int[] startTimes,
        int[] finishTimes
    ) {
        ActivityInterval[] activities = buildIntervals(startTimes, finishTimes);
        return selectMaximumNonOverlappingActivities(Arrays.asList(activities));
    }

    public static SelectionResult selectMaximumNonOverlappingActivities(
        List<ActivityInterval> activities
    ) {
        List<ActivityInterval> validatedActivities = List.copyOf(
            Objects.requireNonNull(activities, "activities")
        );
        if (validatedActivities.isEmpty()) {
            return new SelectionResult(List.of());
        }

        ActivityInterval[] sortedActivities = validatedActivities.toArray(new ActivityInterval[0]);
        sortByFinishTime(sortedActivities);
        return selectActivities(sortedActivities);
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

    private static ActivityInterval[] buildIntervals(int[] startTimes, int[] finishTimes) {
        Objects.requireNonNull(startTimes, "startTimes");
        Objects.requireNonNull(finishTimes, "finishTimes");

        if (startTimes.length != finishTimes.length) {
            throw new IllegalArgumentException("startTimes and finishTimes must have the same length");
        }

        ActivityInterval[] activities = new ActivityInterval[startTimes.length];
        for (int i = 0; i < startTimes.length; i++) {
            if (finishTimes[i] < startTimes[i]) {
                throw new IllegalArgumentException(
                    "finish time cannot be earlier than start time at index " + i
                );
            }
            activities[i] = new ActivityInterval(startTimes[i], finishTimes[i]);
        }
        return activities;
    }

    private static ActivityInterval[] sortByFinishTime(ActivityInterval[] activities) {
        Arrays.sort(activities, BY_FINISH_THEN_START);
        return activities;
    }

    private static SelectionResult selectActivities(ActivityInterval[] sortedActivities) {
        List<ActivityInterval> selectedActivities = new ArrayList<>(sortedActivities.length);
        selectedActivities.add(sortedActivities[0]);
        int lastSelectedFinish = sortedActivities[0].finishTime();

        for (int i = 1; i < sortedActivities.length; i++) {
            ActivityInterval candidate = sortedActivities[i];
            if (candidate.startTime() >= lastSelectedFinish) {
                selectedActivities.add(candidate);
                lastSelectedFinish = candidate.finishTime();
            }
        }

        return new SelectionResult(List.copyOf(selectedActivities));
    }

    public record ActivityInterval(int startTime, int finishTime) {
        public ActivityInterval {
            if (finishTime < startTime) {
                throw new IllegalArgumentException("finishTime cannot be earlier than startTime");
            }
        }
    }

    public record SelectionResult(List<ActivityInterval> selectedActivities) {
        public SelectionResult {
            Objects.requireNonNull(selectedActivities, "selectedActivities");
            selectedActivities = List.copyOf(selectedActivities);
        }

        public int count() {
            return selectedActivities.size();
        }

        public boolean isEmpty() {
            return selectedActivities.isEmpty();
        }
    }
}
