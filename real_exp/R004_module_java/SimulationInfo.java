import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Utility class to store some information about the class, including the list of species (as a list of classes)
 * and the default colours used in the simulation.
 *
 * Additional fields are included for testing and optimization when TestingMain is run
 * (to optimize probabilities for a more stable population)
 */
public class SimulationInfo {
    /**
     * The list of the classes of all actors in the simulation
     */
    public static final List<Class> ALL_ACTORS = new ArrayList<>(Arrays.asList(Grass.class, Deer.class, Coyote.class, Wolf.class, Eagle.class, Hunter.class, Mouse.class));

    /**
     * A Map connecting each class in the simulation to a default colour
     */
    public static final Map<Class<?>, Color> DEFAULT_COLOR_MAP = Map.ofEntries(
            Map.entry(Deer.class, Color.ORANGE),
            Map.entry(Coyote.class, Color.BLUE),
            Map.entry(Wolf.class, Color.RED),
            Map.entry(Mouse.class, Color.PINK),
            Map.entry(Grass.class, Color.GREEN),
            Map.entry(Hunter.class, Color.BLACK),
            Map.entry(Eagle.class, Color.MAGENTA)
    );

    /**
     * Utility fields to help with the running of TestingMain
     */
    public static Integer HIGHEST_STEPS = 0;
    public static Map<Class<?>, Double> HIGHEST_STEP_PROBS;

}
