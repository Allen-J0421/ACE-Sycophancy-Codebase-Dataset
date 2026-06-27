import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Responsible for populating the simulation field with organisms, water
 * sources, diseases, and weather, and for managing dynamic creation events
 * such as rainfall and spontaneous disease outbreaks.
 *
 * @version 2022.03.01
 */
public class PopulationManager
{
    private final Field field;
    private final Simulator simulator;
    // Diseases are held here because they are created during populate() and
    // consulted each step by generateDisease().
    private List<Disease> diseases;

    /**
     * @param field     The simulation field to populate.
     * @param simulator The owning simulator (required by Weather).
     */
    public PopulationManager(Field field, Simulator simulator)
    {
        this.field = field;
        this.simulator = simulator;
        diseases = new ArrayList<>();
    }

    /**
     * Clear the field and populate it from scratch with all entity types.
     * @return A list of all actors to be added to the simulation.
     */
    public List<Actor> populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();

        List<Actor> actors = new ArrayList<>();

        diseases = new ArrayList<>();
        Disease covid = new Covid(field);
        Disease leptospirosis = new Leptospirosis(field);
        diseases.addAll(Arrays.asList(covid, leptospirosis));

        actors.addAll(diseases);
        actors.add(new Weather(simulator));

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Location location = new Location(row, col);
                if(rand.nextDouble() <= SimulationConfiguration.LAKE_CREATION_PROBABILITY) {
                    Lake lake = new Lake(true, field, location);
                    actors.add(lake);
                    // Spread lake over adjacent cells to create realistic water bodies.
                    for(Location adj : field.adjacentLocations(location)) {
                        actors.add(new Lake(true, field, adj));
                    }
                }
                else if(rand.nextDouble() <= SimulationConfiguration.HYENA_CREATION_PROBABILITY) {
                    actors.add(new Hyena(true, field, location));
                }
                else if(rand.nextDouble() <= SimulationConfiguration.LION_CREATION_PROBABILITY) {
                    actors.add(new Lion(true, field, location));
                }
                else if(rand.nextDouble() <= SimulationConfiguration.GAZELLE_CREATION_PROBABILITY) {
                    actors.add(new Gazelle(true, field, location));
                }
                else if(rand.nextDouble() <= SimulationConfiguration.MOUSE_CREATION_PROBABILITY) {
                    actors.add(new Mouse(true, field, location));
                }
                else if(rand.nextDouble() <= SimulationConfiguration.FENNECFOX_CREATION_PROBABILITY) {
                    actors.add(new FennecFox(true, field, location));
                }
                else if(rand.nextDouble() <= SimulationConfiguration.GRASS_CREATION_PROBABILITY) {
                    actors.add(new Grass(true, field, location));
                }
            }
        }
        return actors;
    }

    /**
     * Create new lake tiles across the field to simulate rainfall.
     * @return A list of newly created water source actors.
     */
    public List<Actor> generateWater()
    {
        Random rand = Randomizer.getRandom();
        List<Actor> newWater = new ArrayList<>();
        double rainLakeProbability = SimulationConfiguration.LAKE_CREATION_PROBABILITY
                                     / SimulationConfiguration.GRID_DEPTH;
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() < rainLakeProbability) {
                    Location location = new Location(row, col);
                    newWater.add(new Lake(true, field, location));
                    for(Location adj : field.adjacentLocations(location)) {
                        if(rand.nextBoolean()) {
                            newWater.add(new Lake(true, field, adj));
                        }
                    }
                }
            }
        }
        return newWater;
    }

    /**
     * Give each active disease a small random chance to spontaneously infect
     * the given actor.
     * @param actor The actor that may become infected.
     */
    public void generateDisease(Actor actor)
    {
        Random rand = Randomizer.getRandom();
        for(Disease disease : diseases) {
            if(rand.nextDouble() <= disease.getProbability()) {
                if(disease.getSpecies().contains(actor.getClass().getName())) {
                    disease.addIndividual(actor);
                    Organism organism = (Organism) actor;
                    organism.setInfected();
                }
            }
        }
    }
}
