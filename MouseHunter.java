import java.util.List;

/**
 * Shared behavior for predators that hunt mice.
 */
public abstract class MouseHunter extends BreedingAnimal
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
    public final int foodValue()
    {
        return foodValue;
    }

    /**
     * Create a newborn of the concrete species.
     */
    protected abstract Animal createYoung(Field field, Location location);

    /**
     * @return The maximum age for this species.
     */
    @Override
    protected final int getMaxAge()
    {
        return maxAge;
    }

    @Override
    protected final int getBreedingAge()
    {
        return breedingAge;
    }

    @Override
    protected final double getBreedingProbability()
    {
        return breedingProbability;
    }

    @Override
    protected final int getMaxLitterSize()
    {
        return maxLitterSize;
    }

    @Override
    protected final void updateStatus(SimulationStep step)
    {
        decrementFoodLevel();
    }

    @Override
    protected final void handleAliveStep(List<Organism> newOrganisms, SimulationStep step)
    {
        breed(newOrganisms);
    }

    @Override
    protected final Location selectMoveLocation(SimulationStep step)
    {
        return findFood();
    }

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
