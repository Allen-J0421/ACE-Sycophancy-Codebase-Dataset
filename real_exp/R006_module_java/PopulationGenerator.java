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
      
      // Nested loop to go through every location on the grid and generate an animal of a certain class 
      // based on a comparison of randomised double with the creation probability of every animal
      for(int row = 0; row < field.getDepth(); row++) {
          for(int col = 0; col < field.getWidth(); col++) {
              Location location = new Location(row, col);
              if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {                  
                  animals.add(carnivoreFactory.getAnimal("FOX", location));
              }
              else if(rand.nextDouble() <= REINDEER_CREATION_PROBABILITY) {
                  animals.add(herbivoreFactory.getAnimal("SHEEP", location));
              }
              else if (rand.nextDouble() <= SHEEP_CREATION_PROBABILITY) {
                  animals.add(herbivoreFactory.getAnimal("REINDEER", location));
              }
              else if (rand.nextDouble() <= BEAR_CREATION_PROBABILITY) {
                  animals.add(carnivoreFactory.getAnimal("BEAR", location));
              }
              else if (rand.nextDouble() <= WOLVERINE_CREATION_PROBABILITY) {
                  animals.add(carnivoreFactory.getAnimal("WOLVERINE", location));
              }
                // else leave the location empty.
          }
      }
        
      // A loop to go through the list of generated animals and infect 11 animals
      for (int i = 0; i < INITIAL_INFECTION_COUNT; i++) {
          if (animals.get(i) instanceof Animal){
              Animal animal = (Animal) (animals.get(i));
              animal.setInfectionTimestamp(0);
          }    
      }
      
      // Nested loop to go through every location on the grid and generate a plant of a certain class 
      // based on a comparison of randomised double with the creation probability of every plant
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