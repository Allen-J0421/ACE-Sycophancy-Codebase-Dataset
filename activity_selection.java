import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Solves the classic Activity Selection problem with a greedy strategy.
 *
 * <p>Given a set of activities, each with a start and finish time, the goal is to
 * select the maximum-size subset of mutually compatible activities (no two of the
 * chosen activities overlap in time). The optimal greedy approach is to repeatedly
 * pick the compatible activity that finishes earliest.
 *
 * <p>Two activities are <em>compatible</em> when one starts strictly after the other
 * finishes (i.e. {@code next.start() > current.finish()}); back-to-back activities
 * that touch at a single instant are treated as overlapping.
 */
public class ActivitySelection {

    /**
     * An activity with a start time and a finish time. Immutable.
     *
     * <p>Activities order naturally by finish time, which is the order in which the
     * greedy algorithm considers them.
     */
    public record Activity(int start, int finish) implements Comparable<Activity> {
        public Activity {
            if (start > finish) {
                throw new IllegalArgumentException(
                    "start (" + start + ") must not be after finish (" + finish + ")");
            }
        }

        /**
         * Whether this activity can be scheduled after {@code earlier} without
         * overlapping it, i.e. it starts strictly after {@code earlier} finishes.
         * Activities that merely touch at a single instant are not compatible.
         */
        public boolean startsAfter(Activity earlier) {
            return start > earlier.finish;
        }

        @Override
        public int compareTo(Activity other) {
            return Integer.compare(finish, other.finish);
        }
    }

    private ActivitySelection() {
        // Utility class; not meant to be instantiated.
    }

    /**
     * Returns the size of a maximum set of mutually compatible activities.
     *
     * @param start  start times
     * @param finish finish times (parallel to {@code start})
     * @return the number of activities in an optimal selection
     */
    public static int activitySelection(int[] start, int[] finish) {
        return select(toActivities(start, finish)).size();
    }

    /**
     * Returns a maximum set of mutually compatible activities, in the order they
     * are scheduled (by finish time).
     *
     * @param activities the candidate activities (not modified)
     * @return an optimal selection; empty if {@code activities} is empty
     */
    public static List<Activity> select(List<Activity> activities) {
        Objects.requireNonNull(activities, "activities");
        if (activities.isEmpty()) {
            return new ArrayList<>();
        }

        List<Activity> byFinish = new ArrayList<>(activities);
        byFinish.sort(null); // natural order: by finish time

        List<Activity> selected = new ArrayList<>();
        Activity last = null;
        for (Activity candidate : byFinish) {
            if (last == null || candidate.startsAfter(last)) {
                selected.add(candidate);
                last = candidate;
            }
        }
        return selected;
    }

    /**
     * Pairs parallel start/finish arrays into {@link Activity} values.
     *
     * @throws NullPointerException     if either array is null
     * @throws IllegalArgumentException if the arrays differ in length
     */
    private static List<Activity> toActivities(int[] start, int[] finish) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(finish, "finish");
        if (start.length != finish.length) {
            throw new IllegalArgumentException(
                "start and finish must have equal length: "
                    + start.length + " != " + finish.length);
        }

        List<Activity> activities = new ArrayList<>(start.length);
        for (int i = 0; i < start.length; i++) {
            activities.add(new Activity(start[i], finish[i]));
        }
        return activities;
    }

    public static void main(String[] args) {
        int[] start = {1, 3, 0, 5, 8, 5};
        int[] finish = {2, 4, 6, 7, 9, 9};

        List<Activity> chosen = select(toActivities(start, finish));
        System.out.println("Maximum activities: " + chosen.size());
        System.out.println("Selected: " + chosen);
    }
}
