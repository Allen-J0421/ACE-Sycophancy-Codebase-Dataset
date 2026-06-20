final class Activity {
    private final int startTime;
    private final int finishTime;

    Activity(int startTime, int finishTime) {
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    int getStartTime() {
        return startTime;
    }

    int getFinishTime() {
        return finishTime;
    }
}
