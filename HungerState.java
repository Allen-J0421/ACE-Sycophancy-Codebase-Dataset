/**
 * Mutable hunger-related state for an organism.
 *
 * @version 26/02/2022
 */
public class HungerState
{
    private int foodLevel;
    private int maxFoodLevel;

    public int getFoodLevel()
    {
        return foodLevel;
    }

    public void setFoodLevel(int foodLevel)
    {
        this.foodLevel = foodLevel;
    }

    public int getMaxFoodLevel()
    {
        return maxFoodLevel;
    }

    public void setMaxFoodLevel(int maxFoodLevel)
    {
        this.maxFoodLevel = maxFoodLevel;
    }
}
