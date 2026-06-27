/**
 * A simple model of a Deer.
 * Deers age, move, eat grass, breed, and die.
 *
 * @version 2022.03.02 
 */
public class Deer extends BreedingAnimal
{
    private static final AnimalProfile PROFILE = new AnimalProfile(175, 250, 250, 50);
    private static final BreedingProfile BREEDING_PROFILE =
        new BreedingProfile(10, 0.10, 5, true, 2);

    /**
     * Create a new Deer. A Deer may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the Deer will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Deer(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, PROFILE, BREEDING_PROFILE);
    }

    @Override
    protected void updateStatus(SimulationStep step)
    {
        decrementFoodLevel();
    }

    @Override
    protected Animal createYoung(Field field, Location location)
    {
        return new Deer(false, field, location);
    }
    
    /**
     * Look for grass adjacent to the current location.
     * Only the first live grass is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    @Override
    protected Location selectMoveLocation(SimulationStep step)
    {
        if(step.getWeather() == Weather.RAINY) {
            return null;
        }
        return findFood();
    }

    private Location findFood()
    {
        return findAdjacentLocation(Grass.class, 1, grass -> {
            if(grass.isAlive()) {
                if(grass.getSize() > 12 && getFoodLevel() < 30) {
                    grass.decrementSize();
                    changeFoodLevel(20);
                }
                return true;
            }
            return false;
        });
    }
}
