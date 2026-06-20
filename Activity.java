final class Activity {
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
