import java.awt.Color;

/**
 * Observer interface for simulation lifecycle events.
 * Decouples Simulator from any specific UI implementation.
 */
public interface SimulationObserver {
    void showStatus(int step, Field field, int time, String weather);
    boolean isViable(Field field);
    void onColorRegistered(Class<?> entityClass, Color color);
}
