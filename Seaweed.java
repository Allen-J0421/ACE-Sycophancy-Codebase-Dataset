import java.util.List;
import java.util.Random;

/**
  * A simple model of a seaweed.
 * 
 * Seaweed age, consume oxygen during the night and produce oxygen during the daytime, propogate, 
 * and may die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Seaweed extends Creature
{
   
    // The age at which a seaweed becomes mature and could be edible.
    private static final int MATURE_AGE = 20;
    // The age to which a shrimp can live.
    private static final int MAX_AGE = 40;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // The amount of oxygen a plant need to survive in night.
    private static final double PLANT_OXYGEN_REQUIRED = 0.00000002;
    // The amount of oxygen a plant generated during day time.
    private static final double OXYGEN_GENERATED = 0.0000005;
    
     
    // Individual characteristics (instance fields).
    
    // The seaweed's age.
    private int age;
    
    /**
     * Constructor for objects of class Seaweed
     */
    public Seaweed(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }  
    }

   /**
     * This is what the seawead does most of the time - stay motionlessly under the water
     * @param newSeaweeds A list to return newly born shrimps.
     * @param atDayTime true if current step is daytime false otherwise.
     * @param oxygenLevel The inital level of dissolved oxygen in the water.
     * @param disease The disease may happened during simulation.  
     * @param step current step.
     * 
     * @return the oxygen level the species produced or consumed after action.
     */
    public double act(List<Creature> newSeaweeds, SimulationContext context)
    {
        incrementAge();
        if(context.getOxygenLevel() < PLANT_OXYGEN_REQUIRED) {
            setDead();
            return 0;
        }

        if(isAlive() && age >= MATURE_AGE) {
            giveBirth(newSeaweeds);
        }

        if(context.isTimeOfDay()) {
            return OXYGEN_GENERATED;
        }
        else {
            return -PLANT_OXYGEN_REQUIRED;
        }
    }
    
    /**
     * Increase the age.
     * This could result in the shrimp's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this shrimp is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newSeaweeds A list to return newly propagated seaweed.
     */
    private void giveBirth(List<Creature> newSeaweeds)
    {
        // New seaweed are created into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());

        while(free.size() > 0) {
            Location loc = free.remove(0);
            Seaweed young = new Seaweed(false, field, loc);
            newSeaweeds.add(young);
        }
    }
}
