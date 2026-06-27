import java.awt.Color;

/**
 * Strategy for rendering cell colors in the simulator grid.
 */
public interface CellColorStrategy
{
    void setColor(Class<?> animalClass, Color color);

    void render(SimulationDisplayContext context, GridCanvas canvas);
}
