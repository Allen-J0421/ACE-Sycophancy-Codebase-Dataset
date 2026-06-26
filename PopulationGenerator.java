import java.util.Random;
/**
 * A class responsible for generating the population,
 * as well as setting up the colours associated with each actor type.
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

    @FunctionalInterface
    private interface PopulationAdder<T extends Actor>
    {
        void add(SimulationPopulation population, T actor);
    }
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Construct a population generator and set up the colours for each supported actor type.
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
     * The RGB colours associated with the supported actor types.
     */
    private void setUpColors()
    {
        registerColors(AnimalSpecies.values());
        registerColors(PlantSpecies.values());
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
        populateActors(population, ANIMAL_SPAWN_ORDER, animalFactory, SimulationPopulation::addAnimal);
        population.infectInitialAnimals(INITIAL_INFECTION_COUNT);
        populateActors(population, PLANT_SPAWN_ORDER, plantFactory, SimulationPopulation::addPlant);
    }

    private void registerColors(SpawnableActorType<?>[] actorTypes)
    {
        for (SpawnableActorType<?> actorType : actorTypes) {
            view.setColor(actorType, actorType.getColor());
        }
    }

    private <T extends Actor, S extends SpawnableActorType<T>> void populateActors(
        SimulationPopulation population,
        S[] spawnOrder,
        ActorFactory<T, S> factory,
        PopulationAdder<T> populationAdder
    ) {
        Random rand = Randomizer.getRandom();
        field.forEachLocation((row, col) -> {
            Location location = new Location(row, col);
            T actor = createActorAt(rand, location, spawnOrder, factory);
            if(actor != null) {
                populationAdder.add(population, actor);
            }
        });
    }

    private <T extends Actor, S extends SpawnableActorType<T>> T createActorAt(
        Random rand,
        Location location,
        S[] spawnOrder,
        ActorFactory<T, S> factory
    ) {
        for (S species : spawnOrder) {
            if(rand.nextDouble() <= species.getSpawnProbability()) {
                return factory.create(species, location);
            }
        }
        return null;
    }
}
