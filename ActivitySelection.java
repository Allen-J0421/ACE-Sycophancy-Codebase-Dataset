import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ActivitySelection {
    private static final Comparator<ActivitySlot> BY_FINISH_TIME =
        Comparator.comparingInt(ActivitySlot::finishTime);

    private ActivitySelection() {
    }

    public static int activitySelection(int[] start, int[] finish) {
        return maximumCompatibleActivityCount(start, finish);
    }

    public static int maximumCompatibleActivityCount(int[] start, int[] finish) {
        return buildSelectionResult(start, finish).count();
    }

    public static int[][] selectMaximumCompatibleActivities(int[] start, int[] finish) {
        return buildSelectionResult(start, finish).activities();
    }

    public static SelectionResult buildSelectionResult(int[] start, int[] finish) {
        return ActivitySchedule.fromParallelArrays(start, finish).selectionResult();
    }

    public static void main(String[] args) {
        int[] start = {1, 3, 0, 5, 8, 5};
        int[] finish = {2, 4, 6, 7, 9, 9};

        System.out.println(activitySelection(start, finish));
    }

    private static final class ActivitySchedule {
        private final ActivitySlot[] activities;

        private ActivitySchedule(ActivitySlot[] activities) {
            this.activities = activities;
        }

        private static ActivitySchedule fromParallelArrays(int[] start, int[] finish) {
            validateInputs(start, finish);

            ActivitySlot[] activities = new ActivitySlot[start.length];
            for (int i = 0; i < start.length; i++) {
                activities[i] = new ActivitySlot(start[i], finish[i]);
            }

            Arrays.sort(activities, BY_FINISH_TIME);
            return new ActivitySchedule(activities);
        }

        private SelectionResult selectionResult() {
            return new SelectionResult(selectCompatibleActivities());
        }

        private List<ActivitySlot> selectCompatibleActivities() {
            if (activities.length == 0) {
                return List.of();
            }

            List<ActivitySlot> selectedActivities = new ArrayList<>();
            ActivitySlot lastSelected = activities[0];
            selectedActivities.add(lastSelected);

            // Greedily keep the earliest-finishing compatible activity.
            for (int i = 1; i < activities.length; i++) {
                ActivitySlot current = activities[i];
                if (current.canFollow(lastSelected)) {
                    selectedActivities.add(current);
                    lastSelected = current;
                }
            }

            return selectedActivities;
        }

        private static void validateInputs(int[] start, int[] finish) {
            Objects.requireNonNull(start, "start must not be null");
            Objects.requireNonNull(finish, "finish must not be null");

            if (start.length != finish.length) {
                throw new IllegalArgumentException("start and finish must have the same length");
            }
        }
    }

    public static final class SelectionResult {
        private final List<ActivitySlot> activities;

        private SelectionResult(List<ActivitySlot> activities) {
            this.activities = List.copyOf(activities);
        }

        public int count() {
            return activities.size();
        }

        public List<ActivitySlot> selectedActivities() {
            return activities;
        }

        public int[][] activities() {
            int[][] pairs = new int[activities.size()][2];
            for (int i = 0; i < activities.size(); i++) {
                pairs[i] = activities.get(i).toPair();
            }
            return pairs;
        }
    }

    public static record ActivitySlot(int startTime, int finishTime) {
        private boolean canFollow(ActivitySlot other) {
            return startTime > other.finishTime;
        }

        private int[] toPair() {
            return new int[] {startTime, finishTime};
        }
    }
}
