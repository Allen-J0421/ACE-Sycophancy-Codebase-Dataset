import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collection;
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
        return Schedule.from(startTimes, finishTimes).selectMaximumNonOverlappingActivities();
    }

    public static SelectionResult selectMaximumNonOverlappingActivities(
        Collection<ActivityInterval> activities
    ) {
        return Schedule.from(activities).selectMaximumNonOverlappingActivities();
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

    private static List<ActivityInterval> buildIntervals(int[] startTimes, int[] finishTimes) {
        Objects.requireNonNull(startTimes, "startTimes");
        Objects.requireNonNull(finishTimes, "finishTimes");

        if (startTimes.length != finishTimes.length) {
            throw new IllegalArgumentException("startTimes and finishTimes must have the same length");
        }

        List<ActivityInterval> activities = new ArrayList<>(startTimes.length);
        for (int i = 0; i < startTimes.length; i++) {
            if (finishTimes[i] < startTimes[i]) {
                throw new IllegalArgumentException(
                    "finish time cannot be earlier than start time at index " + i
                );
            }
            activities.add(new ActivityInterval(startTimes[i], finishTimes[i]));
        }
        return activities;
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

    private record Schedule(List<ActivityInterval> activities) {
        private Schedule {
            Objects.requireNonNull(activities, "activities");
            activities = List.copyOf(activities);
        }

        static Schedule from(int[] startTimes, int[] finishTimes) {
            return new Schedule(buildIntervals(startTimes, finishTimes));
        }

        static Schedule from(Collection<ActivityInterval> activities) {
            return new Schedule(List.copyOf(Objects.requireNonNull(activities, "activities")));
        }

        SelectionResult selectMaximumNonOverlappingActivities() {
            if (activities.isEmpty()) {
                return new SelectionResult(List.of());
            }

            List<ActivityInterval> sortedActivities = new ArrayList<>(activities);
            sortedActivities.sort(BY_FINISH_THEN_START);
            return selectGreedy(sortedActivities);
        }

        private static SelectionResult selectGreedy(List<ActivityInterval> sortedActivities) {
            List<ActivityInterval> selectedActivities = new ArrayList<>(sortedActivities.size());
            ActivityInterval firstActivity = sortedActivities.get(0);
            selectedActivities.add(firstActivity);
            int lastSelectedFinish = firstActivity.finishTime();

            for (int i = 1; i < sortedActivities.size(); i++) {
                ActivityInterval candidate = sortedActivities.get(i);
                if (candidate.startTime() >= lastSelectedFinish) {
                    selectedActivities.add(candidate);
                    lastSelectedFinish = candidate.finishTime();
                }
            }

            return new SelectionResult(selectedActivities);
        }
    }
}
