import java.util.List;
import java.util.Random;
/**
 * A simple model of a tree
 * Trees age, hydrate, and die.
 *
 * @version 2022.25.02
 */
public class Tree extends Nature
{
    // Initialise random number generator
    Random random = new Random();
    
    // The age to which a Tree can live.
    private static final int MAX_AGE = 720;
    // The number of steps a Tree can last for until it needs water again
    private static final int RABBIT_FOOD_VALUE = 360;
    
    // Individual characteristics (instance fields).
    // The Tree's age.
    private int age;
    // The Tree's food level, which is increased by getting watered.
    private int hydrationLevel;
    
    /**
     * Create a new Tree. A Tree may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Tree will have a random age.
     * @param field The field currently occupied.
     * @param natureField The nature field currently occupied
     * @param location The location within the field.
     */
    public Tree(boolean randomAge, Field field, NatureField natureField, Location location)
    {
        super(field, natureField, location);
        age = 0;
        hydrationLevel = RABBIT_FOOD_VALUE;
        if(randomAge) {
            age = random.nextInt(MAX_AGE);
            hydrationLevel = random.nextInt(RABBIT_FOOD_VALUE);
        }
        else {
            age = 0;
            hydrationLevel = RABBIT_FOOD_VALUE;
        }
        
        setFieldLocation(location);
    }
    
    // Default constructor for dummy objects
    public Tree()
    {
    }

    /**
     * This is what the Tree does most of the time: grows.
     * In the process, grows, or it might die of thirst,
     * or die of old age.
     * @param newTrees A list to return newly born Trees.
     * @param isRaining Whether it is currently raining in the environment
     */
    public void act(List<Nature> newTrees, boolean isRaining)
    {
        incrementAge();
        isHydratePossible(isRaining);
        
    }
    
    /**
     * Increase the age. This could result in the Tree's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this Tree more hungry. This could result in the Tree's death.
     */
    private void incrementThirst()
    {
        hydrationLevel--;
        if(hydrationLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Rehydrate the Tree depending if rain is currently present in environment.
     * @param isRaining Whether there is currently rain in the environment
     */
    private void isHydratePossible(boolean isRaining)
    {
        if (isRaining) {
            hydrationLevel = RABBIT_FOOD_VALUE;
        }
        else {
            incrementThirst();
        }
    }
}
