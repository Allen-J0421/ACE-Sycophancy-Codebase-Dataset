public class ActivitySelector implements SelectionStrategy {
    public int select(ActivityCollection collection) {
        ActivityCollection sorted = collection.sortedByFinish();

        int count = 1;
        int j = 0;

        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).start > sorted.get(j).finish) {
                count++;
                j = i;
            }
        }

        return count;
    }
}
