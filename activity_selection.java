import java.util.Arrays;
import java.util.Comparator;

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
        int[] start = {1, 3, 0, 5, 8, 5};
        int[] finish = {2, 4, 6, 7, 9, 9};

        System.out.println(activitySelection(start, finish));
    }
}

class Activity {
    final int start;
    final int finish;

    Activity(int start, int finish) {
        this.start = start;
        this.finish = finish;
    }
}

interface SelectionStrategy {
    int select(Activity[] activities);
}

class ActivitySelector implements SelectionStrategy {
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
