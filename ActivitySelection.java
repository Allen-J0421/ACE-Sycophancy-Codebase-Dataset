public class ActivitySelection {

    public static int activitySelection(int[] start, int[] finish) {
        return activitySelection(start, finish, new ActivitySelector());
    }

    public static int activitySelection(int[] start, int[] finish, SelectionStrategy strategy) {
        int n = start.length;
        Activity[] activities = new Activity[n];
        for (int i = 0; i < n; i++) {
            activities[i] = new Activity(start[i], finish[i]);
        }
        return strategy.select(activities);
    }

    public static void main(String[] args) {
        Activity[] activities = {
            Activity.builder().start(1).finish(2).build(),
            Activity.builder().start(3).finish(4).build(),
            Activity.builder().start(0).finish(6).build(),
            Activity.builder().start(5).finish(7).build(),
            Activity.builder().start(8).finish(9).build(),
            Activity.builder().start(5).finish(9).build(),
        };

        System.out.println(new ActivitySelector().select(activities));
    }
}
