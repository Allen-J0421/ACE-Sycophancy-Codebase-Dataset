import java.util.Random;
import java.util.List;
import java.awt.Color;
/**
 * A class responsible for generating the population,
 * as well as setting up the colours associated with each animal and plant class.
 *
 * @version 1.0
 */
public class PopulationGenerator
{

    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/

    // The probability that an animal will be created in any given grid position.
    private static final double FOX_CREATION_PROBABILITY       = 0.09;
    private static final double REINDEER_CREATION_PROBABILITY  = 0.11;
    private static final double SHEEP_CREATION_PROBABILITY     = 0.11;
    private static final double BEAR_CREATION_PROBABILITY      = 0.04;
    private static final double WOLVERINE_CREATION_PROBABILITY = 0.09;
    // The probability that a plant will be created in any given grid position.
    private static final double GRASS_SPAWN_PROBABILITY = 0.09;
    private static final double SAGE_SPAWN_PROBABILITY  = 0.075;
    private static final double SEDGE_SPAWN_PROBABILITY = 0.07;
    // A constant for initial number of infections in the generated population.
    private static final int INITIAL_INFECTION_COUNT = 11;

    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/

    private SimulatorView view;
    private AnimalFactory animalFactory;
    private PlantFactory plantFactory;
    private Field field;

    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Construct a population generator and set up the colours for representation of each animal class.
     */
    public PopulationGenerator(SimulatorView view, Field field)
    {
        this.view = view;
        this.field = field;
        this.animalFactory = new AnimalFactory(field);
        this.plantFactory = new PlantFactory(field);
        setUpColors();
    }

    /**
     * The RGB colours associated with the given animals and plants.
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
        Random rand = Randomizer.getRandom();
        field.clear();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    animals.add(animalFactory.getAnimal(AnimalType.FOX, location));
                } else if(rand.nextDouble() <= REINDEER_CREATION_PROBABILITY) {
                    animals.add(animalFactory.getAnimal(AnimalType.REINDEER, location));
                } else if(rand.nextDouble() <= SHEEP_CREATION_PROBABILITY) {
                    animals.add(animalFactory.getAnimal(AnimalType.SHEEP, location));
                } else if(rand.nextDouble() <= BEAR_CREATION_PROBABILITY) {
                    animals.add(animalFactory.getAnimal(AnimalType.BEAR, location));
                } else if(rand.nextDouble() <= WOLVERINE_CREATION_PROBABILITY) {
                    animals.add(animalFactory.getAnimal(AnimalType.WOLVERINE, location));
                }
            }
        }

        for(int i = 0; i < INITIAL_INFECTION_COUNT; i++) {
            if(animals.get(i) instanceof Animal) {
                ((Animal) animals.get(i)).setInfectionTimestamp(0);
            }
        }

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                if(rand.nextDouble() <= GRASS_SPAWN_PROBABILITY) {
                    plants.add(plantFactory.getPlant(PlantType.GRASS, location));
                } else if(rand.nextDouble() <= SAGE_SPAWN_PROBABILITY) {
                    plants.add(plantFactory.getPlant(PlantType.SAGE, location));
                } else if(rand.nextDouble() <= SEDGE_SPAWN_PROBABILITY) {
                    plants.add(plantFactory.getPlant(PlantType.SEDGE, location));
                }
            }
        }
    }
}
