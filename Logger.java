public class Logger {
    public enum Level {
        DEBUG(0), INFO(1), WARN(2), ERROR(3), NONE(4);

        final int priority;
        Level(int priority) { this.priority = priority; }
    }

    private static Level currentLevel = Level.INFO;
    private static boolean enabled = true;

    public static void setLevel(Level level) {
        currentLevel = level;
    }

    public static void setEnabled(boolean enable) {
        enabled = enable;
    }

    public static void debug(String message) {
        log(Level.DEBUG, message);
    }

    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void warn(String message) {
        log(Level.WARN, message);
    }

    public static void error(String message) {
        log(Level.ERROR, message);
    }

    public static void error(String message, Throwable throwable) {
        log(Level.ERROR, message + ": " + throwable.getMessage());
    }

    private static void log(Level level, String message) {
        if (!enabled || level.priority < currentLevel.priority) {
            return;
        }

        String timestamp = java.time.LocalDateTime.now().toString();
        String formatted = String.format("[%s] [%s] %s", timestamp, level, message);
        System.out.println(formatted);
    }
}
