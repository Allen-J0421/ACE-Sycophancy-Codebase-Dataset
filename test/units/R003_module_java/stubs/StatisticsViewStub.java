/**
 * Test-only no-op stand-in for the subject's JavaFX StatisticsView window
 * (StatisticsView.java is excluded via unit.conf because it needs real JavaFX).
 * Simulator only references StatisticsView.class as the argument of the
 * (stubbed, no-op) Application.launch. Package-private so it may live in a
 * differently named file and never collides with the excluded original.
 */
class StatisticsView extends javafx.application.Application { }
