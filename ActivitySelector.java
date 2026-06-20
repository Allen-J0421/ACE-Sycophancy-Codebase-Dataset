import java.util.List;

final class ActivitySelector {
    private ActivitySelector() {
    }

    static int maximumCompatibleActivities(int[] startTimes, int[] finishTimes) {
        ActivitySchedule schedule = ActivitySchedule.from(startTimes, finishTimes);

        return countCompatibleActivities(schedule.activitiesSortedByFinishTime());
    }

    private static int countCompatibleActivities(List<Activity> activities) {
        int selectedCount = 0;
        Activity lastSelected = null;

        for (Activity candidate : activities) {
            if (lastSelected == null || candidate.startsAfter(lastSelected)) {
                selectedCount++;
                lastSelected = candidate;
            }
        }

        return selectedCount;
    }
}
