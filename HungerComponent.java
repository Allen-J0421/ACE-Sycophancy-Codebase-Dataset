import java.util.Random;

/**
 * Tracks the food level of an entity and signals when it should die of hunger.
 * tick() decrements the food level each step; eat() restores it up to the maximum.
 *
 * @version 2022.03.02
 */
public class HungerComponent
{
    private static final Random rand = Randomizer.getRandom();

    private int foodLevel;
    private final int maxFoodLevel;

    /**
     * @param maxFoodLevel Upper bound on food level.
     * @param randomStart  If true, start at a random food level in [0, maxFoodLevel); otherwise start full.
     */
    public HungerComponent(int maxFoodLevel, boolean randomStart)
    {
        this.maxFoodLevel = maxFoodLevel;
        this.foodLevel    = randomStart ? rand.nextInt(maxFoodLevel) : maxFoodLevel;
    }

    /**
     * Decrease food level by one step.
     * @return true if the entity has starved and should die.
     */
    public boolean tick()
    {
        foodLevel--;
        return foodLevel <= 0;
    }

    /**
     * Consume food, capping at maxFoodLevel.
     * @param amount The food value gained from eating.
     */
    public void eat(int amount)
    {
        foodLevel = Math.min(foodLevel + amount, maxFoodLevel);
    }

    public int getFoodLevel()    { return foodLevel; }
    public int getMaxFoodLevel() { return maxFoodLevel; }

    /** Returns true when the entity has not yet reached its maximum food level (willing to eat). */
    public boolean isNotFull()   { return foodLevel <= maxFoodLevel; }
}
