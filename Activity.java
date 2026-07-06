public class Activity {
    final int start;
    final int finish;

    Activity(int start, int finish) {
        this.start = start;
        this.finish = finish;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int start;
        private int finish;

        public Builder start(int start) {
            this.start = start;
            return this;
        }

        public Builder finish(int finish) {
            this.finish = finish;
            return this;
        }

        public Activity build() {
            return new Activity(start, finish);
        }
    }
}
