import java.util.List;
import java.util.Random;
/**
 * A simple model of a seaweed.
 * Seaweeds grow and die.
 *
 * @version 2016.02.29 (2)
 */
public class Seaweed extends Plant
{
    // Characteristics shared by all seaweeds (class variables).

    // The maximum amount of seaweed it can have, it will not grow infinitely.
    private static final int MAX_AMOUNT = 100;
    
    // A shared random number generator to control growing.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The current amount of a single seaweed.
    private int amount;
  
    /**
     * Create a new seaweed. A seaweed is created with a random amount.
     * 
     * @param randomAge If true, the seaweed will have random amount.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Seaweed(boolean randomAmount, Field field, Location location)
    {
        super(field, location);
        if(randomAmount) {
            amount = rand.nextInt(MAX_AMOUNT) + 1;
        }
    }

    /**
     * The plant will grow by increasing its amount.
     */
    public void act(Weather weather)
    {
        if(weather == Weather.Rainy) {
            incrementAmount();
        }
    }
    
    /**
     * Increase the amount of a single seaweed has. 
     * If it has reached the max amount, it will keep the amount and not increase more.
     */
    private void incrementAmount()
    {
        amount++;
        if(amount > MAX_AMOUNT) {
            amount = MAX_AMOUNT;
            //setDead();
        }
    }
    
    /**
     * Reduce the amount of a single seaweed. 
     */
    public void reduceAmount()
    {
        amount--;
        if(amount <= 0) {
            //setDead();
        }
    }

    /**
     * @return The amount of a single seaweed. 
     */
    public int getAmount()
    {
        return amount;
    }
}
