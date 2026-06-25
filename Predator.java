
/**
 * A class representing shared characteristics of predators.
 *
 * @version 15/03/2022
 */
public abstract class Predator extends Animal
{
    /**
     * Create a new predator at location in field.
     * 
     * @param randomAge If true, the predator will have a random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Predator(boolean randomAge, Field field, Location location)
    {
       super(field, location, randomAge); 
    }
    
    /**
     * Return the predator's hunger status.
     * @param predator The name of the predator that will be checked
     * to see if it's hungry.
     * @return The predator's hunger status.
     */
    protected boolean isHungry(String predator)
    {
        int mins = getClock();
        boolean hungry = true;
        if (predator.equals("Bear")) {
            hungry = true;
            if (mins >= 300 && mins < 1260) { // Check if the time is between 05:00 and 20:59 inclusive.
                hungry = false;
            }
        }
        else if (predator.equals("Panther")) {
            hungry = false;
            if (mins >= 300 && mins < 720) { // Check if the time is between 05:00 and 11:59 inclusive.
                hungry = true;
            }
        }
        return hungry;
    }
}

