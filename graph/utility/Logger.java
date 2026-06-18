package graph.utility;

public interface Logger {
    void debug(String message);

    void info(String message);

    void warn(String message);

    void error(String message, Throwable throwable);

    boolean isDebugEnabled();

    class ConsoleLogger implements Logger {
        private final boolean debugEnabled;

        public ConsoleLogger(boolean debugEnabled) {
            this.debugEnabled = debugEnabled;
        }

        @Override
        public void debug(String message) {
            if (debugEnabled) {
                System.out.println("[DEBUG] " + message);
            }
        }

        @Override
        public void info(String message) {
            System.out.println("[INFO] " + message);
        }

        @Override
        public void warn(String message) {
            System.out.println("[WARN] " + message);
        }

        @Override
        public void error(String message, Throwable throwable) {
            System.err.println("[ERROR] " + message);
            if (throwable != null) {
                throwable.printStackTrace();
            }
        }

        @Override
        public boolean isDebugEnabled() {
            return debugEnabled;
        }
    }

    class NoOpLogger implements Logger {
        @Override
        public void debug(String message) {
        }

        @Override
        public void info(String message) {
        }

        @Override
        public void warn(String message) {
        }

        @Override
        public void error(String message, Throwable throwable) {
        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }
    }
}
