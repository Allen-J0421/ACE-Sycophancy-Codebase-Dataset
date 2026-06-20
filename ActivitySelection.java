public final class ActivitySelection {
    private ActivitySelection() {
    }

    public static int activitySelection(int[] startTimes, int[] finishTimes) {
        return maximumCompatibleActivities(startTimes, finishTimes);
    }

    public static int maximumCompatibleActivities(int[] startTimes, int[] finishTimes) {
        return ActivitySelector.maxCompatibleActivities(startTimes, finishTimes);
    }

    public static void main(String[] args) {
        int[] startTimes = {1, 3, 0, 5, 8, 5};
        int[] finishTimes = {2, 4, 6, 7, 9, 9};

        System.out.println(maximumCompatibleActivities(startTimes, finishTimes));
    }
}
