import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default renderer for simulation cell colors.
 */
public class DefaultCellColorStrategy implements CellColorStrategy
{
    private static final Color EMPTY_COLOR = Color.white;
    private static final Color UNKNOWN_COLOR = Color.gray;
    private static final Color INFECTED_COLOR = Color.green;
    private static final Color STORM_COLOR = Color.blue;

    private final Map<Class<?>, Color> colors;

    public DefaultCellColorStrategy()
    {
        colors = new HashMap<>();
        registerDefaultColors();
    }

    @Override
    public void setColor(Class<?> animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    @Override
    public void render(SimulationDisplayContext context, GridCanvas canvas)
    {
        Field field = context.getField();
        canvas.preparePaint();

        Set<Location> stormLocations = stormLocations(context);
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Object creature = field.getObjectAt(row, col);
                Color color = EMPTY_COLOR;

                if(creature != null) {
                    color = colorFor(creature);
                }

                if(stormLocations.contains(location)) {
                    color = STORM_COLOR;
                }

                canvas.drawMark(col, row, color);
            }
        }

        canvas.repaint();
    }

    private void registerDefaultColors()
    {
        setColor(Cod.class, Color.ORANGE);
        setColor(Salmon.class, Color.YELLOW);
        setColor(Seaweed.class, Color.RED);
        setColor(Shark.class, Color.BLACK);
        setColor(Whale.class, Color.PINK);
        setColor(Weather.class, STORM_COLOR);
    }

    private Color colorFor(Object creature)
    {
        if(creature instanceof Animal) {
            Animal animal = (Animal)creature;
            if(animal.getIsInfected() && !animal.getIsImmuned()) {
                return INFECTED_COLOR;
            }
        }

        Color color = colors.get(creature.getClass());
        if(color == null) {
            return UNKNOWN_COLOR;
        }
        return color;
    }

    private Set<Location> stormLocations(SimulationDisplayContext context)
    {
        Set<Location> locations = new HashSet<>();
        Weather weather = context.getWeather();
        if(weather.getStormStart() && weather.getRandomLocation() != null) {
            locations.addAll(context.getField().adjacentLocationsIncludingSelf(weather.getRandomLocation(), weather.getStormScope()));
        }
        return locations;
    }
}
