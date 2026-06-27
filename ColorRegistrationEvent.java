import java.awt.Color;

/** Published once per species at startup so subscribers can register display colors. */
public class ColorRegistrationEvent {
    public final Class<?> entityClass;
    public final Color color;

    public ColorRegistrationEvent(Class<?> entityClass, Color color) {
        this.entityClass = entityClass;
        this.color = color;
    }
}
