import java.util.List;

/**
 * Shared behavior for predators that hunt mice.
 */
public abstract class MouseHunter extends Animal
{
    private final int breedingAge;
    private final int maxAge;
    private final double breedingProbability;
    private final int maxLitterSize;
    private final int foodValue;

    /**
     * Create a mouse-hunting predator.
     */
    protected MouseHunter(boolean randomAge, Field field, Location location,
                          int breedingAge, int maxAge, double breedingProbability,
                          int maxLitterSize, int defaultFoodLevel, int foodValue)
    {
        super(field, location);
        this.breedingAge = breedingAge;
        this.maxAge = maxAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.foodValue = foodValue;
        initializeAge(randomAge, maxAge);
        initializeFoodLevel(randomAge, defaultFoodLevel, defaultFoodLevel);
    }

    @Override
    public final void act(List<Animal> newAnimals, SimulationStep step)
    {
        incrementAge(maxAge);
        decrementFoodLevel();
        updateBurnStatus(step.getWeather());
        if(isAlive()) {
            addOffspring(newAnimals, calculateBirths(breedingAge, breedingProbability, maxLitterSize),
                         (field, location) -> createYoung(field, location));
            Location newLocation = findFood();
            if(newLocation == null) {
                newLocation = freeAdjacentLocation();
            }
            moveToOrDie(newLocation);
        }
    }

    @Override
    public final int foodValue()
    {
        return foodValue;
    }

    /**
     * Create a newborn of the concrete species.
     */
    protected abstract Animal createYoung(Field field, Location location);

    /**
     * Find and eat an adjacent mouse.
     */
    private Location findFood()
    {
        return findAdjacentLocation(Mouse.class, 1, mouse -> {
            if(mouse.isAlive()) {
                mouse.setDead();
                changeFoodLevel(mouse.foodValue());
                return true;
            }
            return false;
        });
    }
}
