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

class Activity {
    final int start;
    final int finish;

    Activity(int start, int finish) {
        this.start = start;
        this.finish = finish;
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {
        private int start;
        private int finish;

        Builder start(int start) {
            this.start = start;
            return this;
        }

        Builder finish(int finish) {
            this.finish = finish;
            return this;
        }

        Activity build() {
            return new Activity(start, finish);
        }
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
