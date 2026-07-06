import java.util.Arrays;
import java.util.Comparator;

public class ActivitySelector implements SelectionStrategy {
    public int select(Activity[] activities) {
        Activity[] sorted = activities.clone();
        Arrays.sort(sorted, Comparator.comparingInt(a -> a.finish));

        int count = 1;
        int j = 0;

        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i].start > sorted[j].finish) {
                count++;
                j = i;
            }
        }

        return count;
    }
}
