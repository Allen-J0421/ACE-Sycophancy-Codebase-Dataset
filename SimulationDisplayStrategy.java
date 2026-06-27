import java.awt.Color;

/**
 * Strategy interface for rendering simulation frames and formatting display text.
 */
public interface SimulationDisplayStrategy
{
    void setColor(Class<?> animalClass, Color color);

    String formatInfoText(SimulationDisplayContext context);

    String formatPopulationText(Field field);

    void render(SimulationDisplayContext context, GridCanvas canvas);

    boolean isViable(Field field);
}
