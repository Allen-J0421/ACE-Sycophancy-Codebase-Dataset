import java.util.Random;
import java.util.Set;

/**
 * A simple model of Water fern.
 * Water_Fern age, take in water and sunlight, and die.
 *
 * @version 27.02.22
 */
public class Water_Fern extends Plant
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // String name for the Water fern
    public static final String name = "Water_Fern";
    private static final PlantProfile PROFILE = new PlantProfile(
        name, 5, 200, 0.6, 7, false, true,
        new PlantFactory() {
            public Plant create(Time time, Field field, Location location, Set<Disease> parentDiseases)
            {
                if(parentDiseases != null) {
                    return new Water_Fern(time, field, location, parentDiseases);
                }
                return new Water_Fern(time, field, location);
            }
        });

    /**
     * Create Water fern. Water_Fern are created as age zero.
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param parentDiseases The diseases the parent of this Water fern had.
     */
    public Water_Fern(Time time, Field field, Location location, Set<Disease> parentDiseases)
    {
        super(time, field, location, PROFILE, rand, parentDiseases);
    }

    /**
     * Create Water fern. The Water fern are created with a random age.
     * 
     * @param time The time in the simulation.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Water_Fern(Time time, Field field, Location location)
    {
        super(time, field, location, PROFILE, rand);
    }

}
