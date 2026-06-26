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
    private static final double FOX_CREATION_PROBABILITY = 0.09;  
    private static final double REINDEER_CREATION_PROBABILITY = 0.11;
    private static final double SHEEP_CREATION_PROBABILITY = 0.11;
    private static final double BEAR_CREATION_PROBABILITY = 0.04;
    private static final double WOLVERINE_CREATION_PROBABILITY = 0.09;
    // The probability that a plant will be created in any given grid position.
    private static final double GRASS_SPAWN_PROBABILITY = 0.09;
    private static final double SAGE_SPAWN_PROBABILITY = 0.075;
    private static final double SEDGE_SPAWN_PROBABILITY = 0.07;
    // A constant for initial number of infections in the generated population.
    private static final int INITIAL_INFECTION_COUNT = 11;
    
    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/
    
    private SimulatorView view;
    private AnimalFactoryProducer producer;
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
        producer = new AnimalFactoryProducer(field);
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
    public void populate(List<Actor> animals , List<Actor> plants)
    {
      // Set up a randomizer, animal and plant factories
      Random rand = Randomizer.getRandom();
      field.clear();
      AnimalFactory herbivoreFactory = producer.getFactory(false);
      AnimalFactory carnivoreFactory = producer.getFactory(true);
      PlantFactory plantFactory = new PlantFactory(field);

      populateAnimals(animals, rand, herbivoreFactory, carnivoreFactory);
      infectInitialAnimals(animals);
      populatePlants(plants, rand, plantFactory);
    }

    /**
     * Generate the initial animal population.
     */
    private void populateAnimals(List<Actor> animals,
                                 Random rand,
                                 AnimalFactory herbivoreFactory,
                                 AnimalFactory carnivoreFactory)
    {
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Animal animal = createAnimal(location, rand, herbivoreFactory, carnivoreFactory);
                if(animal != null) {
                    animals.add(animal);
                }
            }
        }
    }

    /**
     * Create an animal for a single location, or return null when the location stays empty.
     */
    private Animal createAnimal(Location location,
                                Random rand,
                                AnimalFactory herbivoreFactory,
                                AnimalFactory carnivoreFactory)
    {
        if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
            return carnivoreFactory.getAnimal(AnimalType.FOX, location);
        }
        else if(rand.nextDouble() <= REINDEER_CREATION_PROBABILITY) {
            return herbivoreFactory.getAnimal(AnimalType.SHEEP, location);
        }
        else if (rand.nextDouble() <= SHEEP_CREATION_PROBABILITY) {
            return herbivoreFactory.getAnimal(AnimalType.REINDEER, location);
        }
        else if (rand.nextDouble() <= BEAR_CREATION_PROBABILITY) {
            return carnivoreFactory.getAnimal(AnimalType.BEAR, location);
        }
        else if (rand.nextDouble() <= WOLVERINE_CREATION_PROBABILITY) {
            return carnivoreFactory.getAnimal(AnimalType.WOLVERINE, location);
        }
        return null;
    }

    /**
     * Infect the first generated animals without assuming a minimum population size.
     */
    private void infectInitialAnimals(List<Actor> animals)
    {
        int infectionLimit = Math.min(INITIAL_INFECTION_COUNT, animals.size());
        for(int i = 0; i < infectionLimit; i++) {
            if(animals.get(i) instanceof Animal) {
                Animal animal = (Animal) animals.get(i);
                animal.setInfectionTimestamp(0);
            }
        }
    }

    /**
     * Generate the initial plant population.
     */
    private void populatePlants(List<Actor> plants, Random rand, PlantFactory plantFactory)
    {
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Plant plant = createPlant(location, rand, plantFactory);
                if(plant != null) {
                    plants.add(plant);
                }
            }
        }
    }

    /**
     * Create a plant for a single location, or return null when the location stays empty.
     */
    private Plant createPlant(Location location, Random rand, PlantFactory plantFactory)
    {
        if(rand.nextDouble() <= GRASS_SPAWN_PROBABILITY) {
            return plantFactory.getPlant(PlantType.GRASS, location);
        }
        else if(rand.nextDouble() <= SAGE_SPAWN_PROBABILITY) {
            return plantFactory.getPlant(PlantType.SAGE, location);
        }
        else if(rand.nextDouble() <= SEDGE_SPAWN_PROBABILITY) {
            return plantFactory.getPlant(PlantType.SEDGE, location);
        }
        return null;
    }
}
