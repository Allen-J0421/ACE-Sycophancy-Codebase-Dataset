import java.util.List;
import java.util.Random;
/**
 * A simple model of a weed
 * Weeds age, hydrate, and die.
 *
 * @version 2022.25.02
 */
public class Weed extends Nature
{
    // Initialise random number generator
    Random random = new Random();
    
    // The age to which a Weed can live.
    private static final int MAX_AGE = 300;
    // The number of steps a Weed can last for until it needs water again
    private static final int WATER_THIRST_VALUE = 150;
    
    // Individual characteristics (instance fields).
    // The Weed's age.
    private int age;
    // The Weed's hydration level, which is increased by getting watered.
    private int hydrationLevel;
    
    /**
     * Create a new Weed. A Weed may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Weed will have a random age.
     * @param field The field currently occupied.
     * @param natureField The nature field currently occupied
     * @param location The location within the field.
     */
    public Weed(boolean randomAge, Field field, NatureField natureField, Location location)
    {
        super(field, natureField, location);
        age = 0;
        hydrationLevel = WATER_THIRST_VALUE;
        
        if(randomAge) {
            age = random.nextInt(MAX_AGE);
            hydrationLevel = random.nextInt(WATER_THIRST_VALUE);
        }
        else {
            age = 0;
            hydrationLevel = WATER_THIRST_VALUE;
        }
    }
    
    // Default constructor for dummy objects
    public Weed()
    {
    }

    /**
     * This is what the Weed does most of the time: grows.
     * In the process, grows, or it might die of thirst,
     * or die of old age.
     * @param newWeeds A list to return newly born Weeds.
     * @param isRaining Whether it is currently raining in the environment
     */
    public void act(List<Nature> newWeeds, boolean isRaining)
    {
        incrementAge();
        isHydratePossible(isRaining);
        
    }
    
    /**
     * Increase the age. This could result in the Weed's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this Weed more thirsty. This could result in the Weed's death.
     */
    private void incrementThirst()
    {
        hydrationLevel--;
        if(hydrationLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Rehydrate the Weed if rain is currently present in environment
     */
    private void isHydratePossible(boolean isRaining)
    {
        if (isRaining) {
            hydrationLevel = WATER_THIRST_VALUE;
        }
        else {
            incrementThirst();
        }
    }
}
