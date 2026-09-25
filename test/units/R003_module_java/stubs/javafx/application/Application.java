package javafx.application;

/**
 * Test-only no-op stand-in for javafx.application.Application (JavaFX is not
 * installed). The subject's Simulator only calls Application.launch(StatisticsView.class)
 * on a side thread to open a statistics chart window; here that is a no-op.
 */
public abstract class Application {
    public static void launch(Class<? extends Application> appClass, String... args) { }
    public static void launch(String... args) { }
}
