public record Activity(int start, int finish) {
    public Activity {
        if (start > finish) {
            throw new IllegalArgumentException(
                "start time " + start + " cannot be greater than finish time " + finish);
        }
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
