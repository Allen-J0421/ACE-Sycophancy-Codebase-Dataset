import java.util.List;
import java.util.Random;
/**
 * Responsible for the one-time initialization of the simulation population:
 * placing animals and plants at random field positions according to configured
 * spawn probabilities, then seeding an initial infection.
 *
 * @version 1.0
 */
public class PopulationInitializer
{

    /*///////////////////////////////////////////////////////////////
                                 CONSTANTS
    //////////////////////////////////////////////////////////////*/

    // The probability that an animal will be created in any given grid position.
    private static final double FOX_CREATION_PROBABILITY = 0.09;
    private static final double REINDEER_CREATION_PROBABILITY = 0.11;
    private static final double SHEEP_CREATION_PROBABILITY = 0.11;
    private static final double BEAR_CREATION_PROBABILITY = 0.04;
    private static final double WOLVERINE_CREATION_PROBABILITY = 0.09;
    // The probability that a plant will be created in any given grid position.
    private static final double GRASS_SPAWN_PROBABILITY = 0.09;
    private static final double SAGE_SPAWN_PROBABILITY = 0.075;
    private static final double SEDGE_SPAWN_PROBABILITY = 0.07;
    // The number of animals that start the simulation already infected.
    private static final int INITIAL_INFECTION_COUNT = 11;

    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/

    private final Field field;
    private final AnimalFactoryProducer producer;

    /*///////////////////////////////////////////////////////////////
                              CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Constructs a PopulationInitializer for the given field.
     *
     * @param field The field into which actors will be placed.
     */
    public PopulationInitializer(Field field)
    {
        this.field = field;
        this.producer = new AnimalFactoryProducer(field);
    }

    /*///////////////////////////////////////////////////////////////
                         INITIALIZATION LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Clears the field and populates it with animals and plants according to
     * spawn probabilities. Seeds an initial infection in the first animals placed.
     *
     * @param animals The list to receive newly created animal actors.
     * @param plants  The list to receive newly created plant actors.
     */
    public void populate(List<Actor> animals, List<Actor> plants)
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        AnimalFactory herbivoreFactory = producer.getFactory(false);
        AnimalFactory carnivoreFactory = producer.getFactory(true);
        PlantFactory plantFactory = new PlantFactory(field);

        // Place animals: each cell is tested against every species' creation probability.
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    animals.add(carnivoreFactory.getAnimal("FOX", location));
                }
                else if(rand.nextDouble() <= REINDEER_CREATION_PROBABILITY) {
                    animals.add(herbivoreFactory.getAnimal("SHEEP", location));
                }
                else if(rand.nextDouble() <= SHEEP_CREATION_PROBABILITY) {
                    animals.add(herbivoreFactory.getAnimal("REINDEER", location));
                }
                else if(rand.nextDouble() <= BEAR_CREATION_PROBABILITY) {
                    animals.add(carnivoreFactory.getAnimal("BEAR", location));
                }
                else if(rand.nextDouble() <= WOLVERINE_CREATION_PROBABILITY) {
                    animals.add(carnivoreFactory.getAnimal("WOLVERINE", location));
                }
                // else leave the location empty.
            }
        }

        // Seed an initial infection in the first INITIAL_INFECTION_COUNT animals.
        for(int i = 0; i < INITIAL_INFECTION_COUNT; i++) {
            if(animals.get(i) instanceof Animal) {
                Animal animal = (Animal)(animals.get(i));
                animal.setInfectionTimestamp(0);
            }
        }

        // Place plants: each cell is tested against every plant species' spawn probability.
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                if(rand.nextDouble() <= GRASS_SPAWN_PROBABILITY) {
                    plants.add(plantFactory.getPlant("GRASS", location));
                }
                else if(rand.nextDouble() <= SAGE_SPAWN_PROBABILITY) {
                    plants.add(plantFactory.getPlant("SAGE", location));
                }
                else if(rand.nextDouble() <= SEDGE_SPAWN_PROBABILITY) {
                    plants.add(plantFactory.getPlant("SEDGE", location));
                }
                // else leave the location empty.
            }
        }
    }
}
