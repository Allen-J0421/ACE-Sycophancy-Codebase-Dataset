import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps each actor class to its display color and applies them to a SimulatorView.
 */
public class ColorRegistry
{
    private static final Color GRASSHOPPER_COLOR   = new Color(188, 248, 236);
    private static final Color HARVESTER_ANT_COLOR = new Color(4, 139, 168);
    private static final Color TERMITE_COLOR       = new Color(22, 219, 147);
    private static final Color IMPALA_COLOR        = new Color(239, 234, 90);
    private static final Color PANGOLIN_COLOR      = new Color(242, 158, 76);
    private static final Color AARDVARK_COLOR      = new Color(204, 183, 174);
    private static final Color MONGOOSE_COLOR      = new Color(65, 69, 53);
    private static final Color STAR_GRASS_COLOR    = new Color(125, 97, 103);
    private static final Color RED_OAT_GRASS_COLOR = new Color(164, 3, 111);
    private static final Color ACACIA_COLOR        = new Color(187, 214, 134);
    private static final Color CARCASS_COLOR       = new Color(202, 0, 0);

    private final Map<Class, Color> colors;

    public ColorRegistry()
    {
        colors = new LinkedHashMap<>();
        colors.put(Grasshopper.class,  GRASSHOPPER_COLOR);
        colors.put(HarvesterAnt.class, HARVESTER_ANT_COLOR);
        colors.put(Termite.class,      TERMITE_COLOR);
        colors.put(Impala.class,       IMPALA_COLOR);
        colors.put(Pangolin.class,     PANGOLIN_COLOR);
        colors.put(Aardvark.class,     AARDVARK_COLOR);
        colors.put(Mongoose.class,     MONGOOSE_COLOR);
        colors.put(StarGrass.class,    STAR_GRASS_COLOR);
        colors.put(RedOatGrass.class,  RED_OAT_GRASS_COLOR);
        colors.put(Acacia.class,       ACACIA_COLOR);
        colors.put(Carcass.class,      CARCASS_COLOR);
    }

    /**
     * Register all actor colors with the given view.
     *
     * @param view The SimulatorView to configure.
     */
    public void applyTo(SimulatorView view)
    {
        colors.forEach(view::setColor);
    }
}
