public class ActivitySelector implements SelectionStrategy {
    public int select(ActivityCollection collection) {
        ActivityCollection sorted = collection.sortedByFinish();

        int count = 0;
        Activity last = null;

        for (Activity activity : sorted) {
            if (last == null || activity.start > last.finish) {
                count++;
                last = activity;
            }
        }

        return count;
    }
}
