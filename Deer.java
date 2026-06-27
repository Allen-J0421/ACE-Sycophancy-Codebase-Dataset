import java.util.List;

/**
 * A simple model of a Deer.
 * Deers age, move, eat grass, breed, and die.
 *
 * @version 2022.03.02 
 */
public class Deer extends BreedingAnimal
{
    // Characteristics shared by all Deers (class variables).

    // The age at which a Deer can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a Deer can live.
    private static final int MAX_AGE = 175;
    // The likelihood of a Deer breeding.
    private static final double BREEDING_PROBABILITY = 0.10;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single dear. In effect, this is the
    // number of steps it can go.
    private static final int FOOD_VALUE = 50;
    // The default food value of a single deer.
    private static final int DEFAULT_FOOD_LEVEL = 250;

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
        super(field, location);
        initializeAge(randomAge, MAX_AGE);
        initializeFoodLevel(randomAge, DEFAULT_FOOD_LEVEL, DEFAULT_FOOD_LEVEL);
    }
    
    @Override
    protected int getMaxAge()
    {
        return MAX_AGE;
    }

    @Override
    protected void updateStatus(SimulationStep step)
    {
        decrementFoodLevel();
    }

    @Override
    protected int getBreedingAge()
    {
        return BREEDING_AGE;
    }

    @Override
    protected double getBreedingProbability()
    {
        return BREEDING_PROBABILITY;
    }

    @Override
    protected int getMaxLitterSize()
    {
        return MAX_LITTER_SIZE;
    }

    @Override
    protected boolean requiresMate()
    {
        return true;
    }

    @Override
    protected int getMateSearchRadius()
    {
        return 2;
    }

    @Override
    protected Animal createYoung(Field field, Location location)
    {
        return new Deer(false, field, location);
    }

    @Override
    protected void handleAliveStep(List<Organism> newDeers, SimulationStep step)
    {
        breed(newDeers);
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


    /**
     * @return deer's food value.
     */
    public int foodValue()
    {
        return FOOD_VALUE;
    }
}
