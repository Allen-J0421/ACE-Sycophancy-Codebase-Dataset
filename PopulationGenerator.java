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
    
    // A constant for initial number of infections in the generated population.
    private static final int INITIAL_INFECTION_COUNT = 11;
    private static final AnimalSpecies[] ANIMAL_SPAWN_ORDER = {
        AnimalSpecies.FOX,
        AnimalSpecies.REINDEER,
        AnimalSpecies.SHEEP,
        AnimalSpecies.BEAR,
        AnimalSpecies.WOLVERINE
    };
    private static final PlantSpecies[] PLANT_SPAWN_ORDER = {
        PlantSpecies.GRASS,
        PlantSpecies.SAGE,
        PlantSpecies.SEDGE
    };
    
    /*///////////////////////////////////////////////////////////////
                                   STATE
    //////////////////////////////////////////////////////////////*/
    
    private final SimulatorView view;
    private final AnimalFactory animalFactory;
    private final PlantFactory plantFactory;
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
        animalFactory = new AnimalFactory(field);
        plantFactory = new PlantFactory(field);
        setUpColors();
    }
    
    /**
     * The RGB colours associated with the given animals and plants passed to the constructor of Color class from awt library.
     */
    private void setUpColors()
    {
        for (AnimalSpecies species : AnimalSpecies.values()) {
            view.setColor(species.getActorClass(), species.getColor());
        }
        for (PlantSpecies species : PlantSpecies.values()) {
            view.setColor(species.getActorClass(), species.getColor());
        }
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
        field.clear();
        populateAnimals(animals);
        infectInitialAnimals(animals);
        populatePlants(plants);
    }

    private void populateAnimals(List<Actor> animals)
    {
        Random rand = Randomizer.getRandom();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Animal animal = createAnimalAt(rand, location);
                if(animal != null) {
                    animals.add(animal);
                }
            }
        }
    }

    private void infectInitialAnimals(List<Actor> animals)
    {
        int infectedCount = Math.min(INITIAL_INFECTION_COUNT, animals.size());
        for (int i = 0; i < infectedCount; i++) {
            if (animals.get(i) instanceof Animal){
                Animal animal = (Animal) animals.get(i);
                animal.setInfectionTimestamp(0);
            }
        }
    }

    private void populatePlants(List<Actor> plants)
    {
        Random rand = Randomizer.getRandom();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                Plant plant = createPlantAt(rand, location);
                if(plant != null) {
                    plants.add(plant);
                }
            }
        }
    }

    private Animal createAnimalAt(Random rand, Location location)
    {
        for (AnimalSpecies species : ANIMAL_SPAWN_ORDER) {
            if(rand.nextDouble() <= species.getSpawnProbability()) {
                return animalFactory.create(species, location);
            }
        }
        return null;
    }

    private Plant createPlantAt(Random rand, Location location)
    {
        for (PlantSpecies species : PLANT_SPAWN_ORDER) {
            if(rand.nextDouble() <= species.getSpawnProbability()) {
                return plantFactory.create(species, location);
            }
        }
        return null;
    }
}
