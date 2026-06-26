import java.util.Random;

/**
 * A simple model of a dingo.
 * Dingoes age, move, eat snakes, and die.
 *
 * @version 01.03.22
 */
public class Dingo extends Animal
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // Tunable settings for dingoes.
    private SimulationConfig.AnimalSettings settings;

    /**
     * Create a dingo. A dingo can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the dingo will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Dingo(boolean randomAge, Field field, Location location) {
        this(randomAge, field, location, SimulationConfig.defaultConfig());
    }

    /**
     * Create a dingo with the given simulation configuration.
     *
     * @param randomAge If true, the dingo will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param config The simulation configuration to use.
     */
    public Dingo(boolean randomAge, Field field, Location location, SimulationConfig config) {
        super(field, location, config);
        settings = config.getAnimalSettings(Dingo.class);
        this.setGender();
        if(randomAge) {
            setAge(rand.nextInt(settings.getMaxAge()));
            setFoodLevel(rand.nextInt(settings.getInitialFoodValue()));
        }
        else {
            setAge(0);
            setFoodLevel(settings.getInitialFoodValue());
        }
    }

    protected int getMaxAge() {
        return settings.getMaxAge();
    }

    protected int getBreedingAge() {
        return settings.getBreedingAge();
    }

    protected double getBreedingProbability() {
        return settings.getBreedingProbability();
    }

    protected int getMaxLitterSize() {
        return settings.getMaxLitterSize();
    }

    protected boolean isActive(int time) {
        return isActiveBetween(time, settings.getActiveStart(), settings.getActiveEnd());
    }

    protected Animal createYoung(Field field, Location location) {
        return new Dingo(false, field, location, getConfig());
    }

    protected FoodSource[] getFoodSources() {
        return settings.getFoodSources();
    }

    protected boolean canFindFood() {
        return !getFog() || rand.nextDouble() <= settings.getFogFoodSearchProbability();
    }
}
