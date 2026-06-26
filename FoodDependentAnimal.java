/**
 * An animal that must maintain a food reserve to stay alive.
 */
public abstract class FoodDependentAnimal extends Animal {

    private int foodLevel;

    /**
     * Constructor for a food-dependent animal.
     *
     * @param foodLevel The initial food reserve.
     * @param randomAge Whether the animal should have a random age or not.
     * @param field The field in which the animal resides.
     * @param location The location in which the animal spawns into.
     */
    public FoodDependentAnimal(int foodLevel, boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);
        this.foodLevel = foodLevel;
    }

    /**
     * Increase the animal's food reserve.
     *
     * @param foodValue The value to increment the food reserve by.
     */
    protected void incrementFoodLevel(int foodValue) {
        this.foodLevel += foodValue;
    }

    /**
     * Make this animal more hungry. This could result in death.
     */
    protected void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            remove();
        }
    }
}
