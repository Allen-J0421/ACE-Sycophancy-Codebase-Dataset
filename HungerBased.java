/**
 * Mixin for organisms whose survival depends on food level.
 *
 * @version 26/02/2022
 */
public interface HungerBased extends OrganismContext
{
    HungerState getHungerState();

    default void digestOneStep()
    {
        HungerState hungerState = getHungerState();
        hungerState.setFoodLevel(hungerState.getFoodLevel() - 1);

        if(hungerState.getFoodLevel() <= 0) {
            markDead();
        }
    }

    default boolean isHungry()
    {
        HungerState hungerState = getHungerState();
        return hungerState.getFoodLevel() < hungerState.getMaxFoodLevel();
    }

    default void eatFood(int foodValue)
    {
        HungerState hungerState = getHungerState();
        hungerState.setFoodLevel(hungerState.getFoodLevel() + foodValue);
    }
}
