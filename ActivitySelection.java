import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ActivitySelection {
    private static final Comparator<Activity> BY_FINISH_TIME =
        Comparator.comparingInt(Activity::finishTime);

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

        private ActivitySlot toSlot() {
            return new ActivitySlot(startTime, finishTime);
        }

        @Override
        public String toString() {
            return "[" + startTime + ", " + finishTime + "]";
        }
    }

    private static final class ActivitySchedule {
        private final Activity[] activities;

        private ActivitySchedule(Activity[] activities) {
            this.activities = activities;
        }

        private static ActivitySchedule fromParallelArrays(int[] start, int[] finish) {
            validateInputs(start, finish);

            Activity[] activities = new Activity[start.length];
            for (int i = 0; i < start.length; i++) {
                activities[i] = new Activity(start[i], finish[i]);
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
            Activity lastSelected = activities[0];
            selectedActivities.add(lastSelected.toSlot());

            // Greedily keep the earliest-finishing compatible activity.
            for (int i = 1; i < activities.length; i++) {
                Activity current = activities[i];
                if (current.canFollow(lastSelected)) {
                    selectedActivities.add(current.toSlot());
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
        private int[] toPair() {
            return new int[] {startTime, finishTime};
        }
    }
}
