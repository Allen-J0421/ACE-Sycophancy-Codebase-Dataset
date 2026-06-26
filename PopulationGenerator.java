import java.util.Random;
import java.util.List;
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
    private static final AnimalType[] ANIMAL_SPAWN_ORDER = AnimalType.values();
    // The probability that a plant will be created in any given grid position.
    private static final PlantType[] PLANT_SPAWN_ORDER = PlantType.values();
    // A constant for initial number of infections in the generated population.
    private static final int INITIAL_INFECTION_COUNT = 11;
    
    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/
    
    private final SimulatorView view;
    private final Field field;
    
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
        setUpColors();
    }
    
    /**
     * The RGB colours associated with the given animals and plants passed to the constructor of Color class from awt library.
     */
    private void setUpColors()
    {
        registerColors(AnimalType.values());
        registerColors(PlantType.values());
        view.showColors();
    }

    /**
     * Register the species colors with the view.
     *
     * @param species the species to register.
     */
    private void registerColors(SpeciesDescriptor[] species)
    {
        for (SpeciesDescriptor descriptor : species) {
            view.registerSpecies(descriptor);
        }
    }
    
    /**
     * Populate the grid with animals and plants, infect the animals.
     * 
     * @param animals The list of Actor objects representing animals in the simulation
     * @param plants The list of Plant objects representing plants in the simulation
     */
    public void populate(List<Actor> animals , List<Actor> plants)
    {
      // Set up a randomizer and populate the field from the species registries.
      Random rand = Randomizer.getRandom();
      field.clear();
      
      // Nested loop to go through every location on the grid and generate an animal of a certain class 
      // based on a comparison of randomised double with the creation probability of every animal
      for(int row = 0; row < field.getDepth(); row++) {
          for(int col = 0; col < field.getWidth(); col++) {
              Location location = new Location(row, col);
              Animal spawnedAnimal = spawnAnimal(rand, location);
              if(spawnedAnimal != null) {
                  animals.add(spawnedAnimal);
              }
          }
      }
        
      // A loop to go through the list of generated animals and infect 11 animals
      for (int i = 0; i < INITIAL_INFECTION_COUNT && i < animals.size(); i++) {
          Animal animal = (Animal) (animals.get(i));
          animal.setInfectionTimestamp(0);
      }
      
      // Nested loop to go through every location on the grid and generate a plant of a certain class 
      // based on a comparison of randomised double with the creation probability of every plant
      for(int row = 0; row < field.getDepth(); row++) {
          for(int col = 0; col < field.getWidth(); col++) {
              Location location = new Location(row, col);
              Plant spawnedPlant = spawnPlant(rand, location);
              if(spawnedPlant != null) {
                  plants.add(spawnedPlant);
              }
            }
        }
    } 

    /**
     * Try to create an animal at the current location.
     *
     * @param rand random source used to decide whether spawning occurs.
     * @param location location to populate.
     * @return the spawned animal, or null if no spawn occurs.
     */
    private Animal spawnAnimal(Random rand, Location location)
    {
        for(AnimalType type : ANIMAL_SPAWN_ORDER) {
            if(rand.nextDouble() <= type.getSpawnProbability()) {
                return type.createWithRandomGender(field, location);
            }
        }
        return null;
    }

    /**
     * Try to create a plant at the current location.
     *
     * @param rand random source used to decide whether spawning occurs.
     * @param location location to populate.
     * @return the spawned plant, or null if no spawn occurs.
     */
    private Plant spawnPlant(Random rand, Location location)
    {
        for(PlantType type : PLANT_SPAWN_ORDER) {
            if(rand.nextDouble() <= type.getSpawnProbability()) {
                return type.createForPopulation(field, location);
            }
        }
        return null;
    }
} 
