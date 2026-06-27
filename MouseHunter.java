import java.util.List;

/**
 * Shared behavior for predators that hunt mice.
 */
public abstract class MouseHunter extends BreedingAnimal
{
    /**
     * Create a mouse-hunting predator.
     */
    protected MouseHunter(boolean randomAge, Field field, Location location, AnimalProfile profile,
                          BreedingProfile breedingProfile)
    {
        super(randomAge, field, location, profile, breedingProfile);
    }

    /**
     * Create a newborn of the concrete species.
     */
    protected abstract Animal createYoung(Field field, Location location);

    @Override
    protected final void updateStatus(SimulationStep step)
    {
        decrementFoodLevel();
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
