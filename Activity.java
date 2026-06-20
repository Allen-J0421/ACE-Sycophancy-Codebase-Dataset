import java.util.Comparator;

final class Activity {
    static final Comparator<Activity> BY_FINISH_TIME =
        Comparator.comparingInt(Activity::getFinishTime);

    private final int startTime;
    private final int finishTime;

    Activity(int startTime, int finishTime) {
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    int getFinishTime() {
        return finishTime;
    }

    boolean startsAfter(Activity other) {
        return startTime > other.finishTime;
    }
}
