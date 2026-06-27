import java.util.Random;
import java.util.Set;

/**
 * A simple model of grass.
 * Grass age, take in water and sunlight, and die.
 *
 * @version 27.02.22
 */
public class Grass extends Plant
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // String name for the grass
    public static final String name = "Grass";
    private static final PlantProfile PROFILE = new PlantProfile(
        name, 5, 250, 0.8, 12, true, false,
        new PlantFactory() {
            public Plant create(Time time, Field field, Location location, Set<Disease> parentDiseases)
            {
                if(parentDiseases != null) {
                    return new Grass(time, field, location, parentDiseases);
                }
                return new Grass(time, field, location);
            }
        });

    /**
     * Create grass. Grass are created as age zero.
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this grass had.
     */
    public Grass(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        super(time, field, location, PROFILE, rand, parentDiseases);
    }

    /**
     * Create grass. The grass are created with a random age.
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(Time time, Field field, Location location)
    {
        super(time, field, location, PROFILE, rand);
    }

}
