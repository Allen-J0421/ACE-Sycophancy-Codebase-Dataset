import java.util.Random;

/**
 * A simple model of an ant.
 * Ants age, move, eat acacia and grass, breed, and die.
 *
 * @version 01.03.22
 */
public class Ant extends Animal
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // Tunable settings for ants.
    private SimulationConfig.AnimalSettings settings;

    /**
     * Create an ant. An ant can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the ant will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Ant(boolean randomAge, Field field, Location location) {
        this(randomAge, field, location, SimulationConfig.defaultConfig());
    }

    /**
     * Create an ant with the given simulation configuration.
     *
     * @param randomAge If true, the ant will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param config The simulation configuration to use.
     */
    public Ant(boolean randomAge, Field field, Location location, SimulationConfig config) {
        super(field, location, config);
        settings = config.getAnimalSettings(Ant.class);
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
        return new Ant(false, field, location, getConfig());
    }

    protected FoodSource[] getFoodSources() {
        return settings.getFoodSources();
    }

    protected boolean canTramplePlants() {
        return false;
    }

}
