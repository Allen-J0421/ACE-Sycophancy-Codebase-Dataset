import java.util.List;
import java.awt.Color;
/**
 * A lightweight facade responsible for configuring species colors and
 * delegating population initialization to PopulationInitializer.
 *
 * @version 2.0
 */
public class PopulationGenerator
{

    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/

    private SimulatorView view;
    private PopulationInitializer initializer;

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Construct a population generator and set up the colours for representation of each animal class.
     */
    public PopulationGenerator(SimulatorView view, Field field)
    {
        this.view = view;
        initializer = new PopulationInitializer(field);
        setUpColors();
    }

    /**
     * The RGB colours associated with the given animals and plants passed to the constructor of Color class from awt library.
     */
    private void setUpColors()
    {
        view.setColor(CarnivoreFox.class, new Color(227, 93, 57));
        view.setColor(Grass.class, new Color(50, 184, 121));
        view.setColor(Sage.class, new Color(27, 117, 19));
        view.setColor(Reindeer.class, new Color(217, 162, 147));
        view.setColor(Sheep.class, Color.LIGHT_GRAY);
        view.setColor(Bear.class, new Color(112, 62, 49));
        view.setColor(Wolverine.class, Color.BLACK);
        view.setColor(Sedge.class, new Color(78, 117, 19));
        view.showColors();
    }

    /**
     * Populate the grid with animals and plants, infect the animals.
     *
     * @param animals The list of Actor objects representing animals in the simulation
     * @param plants The list of Plant objects representing plants in the simulation
     */
    public void populate(List<Actor> animals, List<Actor> plants)
    {
        initializer.populate(animals, plants);
    }
}
