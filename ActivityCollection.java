import java.util.Arrays;
import java.util.Comparator;

public class ActivityCollection {
    private final Activity[] activities;

    public ActivityCollection(Activity... activities) {
        this.activities = activities.clone();
    }

    public int size() {
        return activities.length;
    }

    public Activity get(int index) {
        return activities[index];
    }

    public ActivityCollection sortedByFinish() {
        Activity[] sorted = activities.clone();
        Arrays.sort(sorted, Comparator.comparingInt(a -> a.finish));
        return new ActivityCollection(sorted);
    }
}
