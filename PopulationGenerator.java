import java.util.Random;
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
            view.setColor(species, species.getColor());
        }
        for (PlantSpecies species : PlantSpecies.values()) {
            view.setColor(species, species.getColor());
        }
        view.showColors();
    }
    
    /**
     * Populate the grid with animals and plants, infect the animals.
     * 
     * @param population The population to populate.
     */
    public void populate(SimulationPopulation population)
    {
        field.clear();
        populateAnimals(population);
        population.infectInitialAnimals(INITIAL_INFECTION_COUNT);
        populatePlants(population);
    }

    private void populateAnimals(SimulationPopulation population)
    {
        Random rand = Randomizer.getRandom();
        field.forEachLocation((row, col) -> {
            Location location = new Location(row, col);
            Animal animal = createAnimalAt(rand, location);
            if(animal != null) {
                population.addAnimal(animal);
            }
        });
    }

    private void populatePlants(SimulationPopulation population)
    {
        Random rand = Randomizer.getRandom();
        field.forEachLocation((row, col) -> {
            Location location = new Location(row, col);
            Plant plant = createPlantAt(rand, location);
            if(plant != null) {
                population.addPlant(plant);
            }
        });
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
