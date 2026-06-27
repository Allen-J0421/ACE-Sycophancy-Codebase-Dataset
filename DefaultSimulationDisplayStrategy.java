import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default rendering strategy for the simulator UI.
 */
public class DefaultSimulationDisplayStrategy implements SimulationDisplayStrategy
{
    private static final Color EMPTY_COLOR = Color.white;
    private static final Color UNKNOWN_COLOR = Color.gray;
    private static final Color INFECTED_COLOR = Color.green;
    private static final Color STORM_COLOR = Color.blue;

    private static final String DAYTIME_TEXT = "daytime";
    private static final String NIGHT_TEXT = "night";

    private final Map<Class<?>, Color> colors;
    private final FieldStats stats;

    public DefaultSimulationDisplayStrategy()
    {
        colors = new HashMap<>();
        stats = new FieldStats();
        registerDefaultColors();
    }

    @Override
    public void setColor(Class<?> animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    @Override
    public String formatInfoText(SimulationDisplayContext context)
    {
        return "It is: " + (context.isTimeOfDay() ? DAYTIME_TEXT : NIGHT_TEXT)
            + "        Oxygen Level: " + (int)(context.getOxygenLevel() * 100) + "%"
            + "        Storm: " + (context.getWeather().getStormStart() ? "exists" : "subsides");
    }

    @Override
    public String formatPopulationText(Field field)
    {
        return stats.getPopulationDetails(field);
    }

    @Override
    public void render(SimulationDisplayContext context, GridCanvas canvas)
    {
        Field field = context.getField();
        stats.reset();
        canvas.preparePaint();

        Set<Location> stormLocations = stormLocations(context);
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Object creature = field.getObjectAt(row, col);
                Color color = EMPTY_COLOR;

                if(creature != null) {
                    stats.incrementCount(creature.getClass());
                    color = colorFor(creature);
                }

                if(stormLocations.contains(location)) {
                    color = STORM_COLOR;
                }

                canvas.drawMark(col, row, color);
            }
        }

        stats.countFinished();
        canvas.repaint();
    }

    @Override
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
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
