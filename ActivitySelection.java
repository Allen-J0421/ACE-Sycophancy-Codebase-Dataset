import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public final class ActivitySelection {

    private ActivitySelection() {
        // Utility class.
    }

    public static int maximumNonOverlappingActivities(int[] startTimes, int[] finishTimes) {
        Objects.requireNonNull(startTimes, "startTimes");
        Objects.requireNonNull(finishTimes, "finishTimes");

        if (startTimes.length != finishTimes.length) {
            throw new IllegalArgumentException("startTimes and finishTimes must have the same length");
        }

        if (startTimes.length == 0) {
            return 0;
        }

        Activity[] activities = new Activity[startTimes.length];
        for (int i = 0; i < startTimes.length; i++) {
            activities[i] = new Activity(startTimes[i], finishTimes[i]);
        }

        Arrays.sort(
            activities,
            Comparator.comparingInt(Activity::finishTime).thenComparingInt(Activity::startTime)
        );

        int selectedCount = 1;
        int lastSelectedFinish = activities[0].finishTime();

        for (int i = 1; i < activities.length; i++) {
            Activity candidate = activities[i];
            if (candidate.startTime() >= lastSelectedFinish) {
                selectedCount++;
                lastSelectedFinish = candidate.finishTime();
            }
        }

        return selectedCount;
    }

    public static void main(String[] args) {
        int[] startTimes = {1, 3, 0, 5, 8, 5};
        int[] finishTimes = {2, 4, 6, 7, 9, 9};

        System.out.println(maximumNonOverlappingActivities(startTimes, finishTimes));
    }

    private record Activity(int startTime, int finishTime) {}
}
