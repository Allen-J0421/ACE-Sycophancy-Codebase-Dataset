import java.util.List;

final class ActivitySelector {
    private ActivitySelector() {
    }

    static int maximumCompatibleActivities(int[] startTimes, int[] finishTimes) {
        ActivitySchedule schedule = ActivitySchedule.from(startTimes, finishTimes);

        if (schedule.isEmpty()) {
            return 0;
        }

        return countCompatibleActivities(schedule.activitiesSortedByFinishTime());
    }

    private static int countCompatibleActivities(List<Activity> activities) {
        int selectedCount = 1;
        Activity lastSelected = activities.get(0);

        for (int i = 1; i < activities.size(); i++) {
            Activity candidate = activities.get(i);

            if (candidate.startsAfter(lastSelected)) {
                selectedCount++;
                lastSelected = candidate;
            }
        }

        return selectedCount;
    }
}
