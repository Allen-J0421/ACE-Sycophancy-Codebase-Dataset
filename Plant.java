import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An abstract plant organism present in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Plant extends Organism implements Growable, Consumable {

    private final PlantTraits traits;
    private double size;
    private final int foodValue;
    private final boolean poisonous;
    private final double growthRate;
    private double breedingProbability;

    /**
     * Constructor for a plant in the simulation.
     *
     * @param traits    Species-level configuration for this plant.
     * @param poisonous Whether the plant is poisonous or not.
     * @param foodValue The food value of this plant when eaten.
     * @param size      The initial size of this plant.
     * @param randomAge Whether the plant should have a random age or not.
     * @param field     The field in which the plant resides.
     * @param location  The location in which the plant spawns into.
     */
    public Plant(PlantTraits traits, boolean poisonous, int foodValue, double size,
                 boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);
        this.traits    = traits;
        this.size      = size;
        this.foodValue = foodValue;
        this.poisonous = poisonous;
        this.growthRate         = traits.growthRate;
        this.breedingProbability = traits.lowBreedingProbability;
    }

    // ── Organism stat getters (satisfied here so subclasses need not override) ──

    @Override public int    getMaxAge()        { return traits.maxAge; }
    @Override public int    getBreedingAge()   { return traits.breedingAge; }
    @Override public int    getMaxLitterSize() { return traits.maxLitterSize; }

    @Override
    public double getBreedingProbability() { return breedingProbability; }

    // ── Growable interface ─────────────────────────────────────────────────────

    @Override public double getMaxSize()                   { return traits.maxSize; }
    @Override public double getSize()                      { return size; }

    @Override
    public void grow() {
        size = size * growthRate > traits.maxSize ? 1 : size * growthRate;
        if (size == 0) remove();
    }

    // ── Consumable interface ───────────────────────────────────────────────────

    @Override public int     getFoodValue()  { return foodValue; }
    @Override public boolean isPoisonous()   { return poisonous; }

    @Override
    public void setEaten() {
        removeFromField();
    }

    // ── Breeding ───────────────────────────────────────────────────────────────

    @Override
    protected boolean canBreed() {
        return getAge() >= getBreedingAge();
    }

    // ── act() — shared across all plant species ────────────────────────────────

    /**
     * Returns the weather types that boost this plant's breeding probability.
     * Concrete plant classes declare which WeatherTypes trigger the high rate.
     *
     * @return Array of WeatherType values that trigger high breeding probability.
     */
    abstract protected WeatherType[] getBoostWeatherTypes();

    /**
     * Grows and attempts to spread seeds each step, with a boosted breeding
     * probability whenever any of the boost weather types appear in recent history.
     */
    @Override
    public void act(List<Organism> newPlants, Weather weather, TimeOfDay time) {
        if (isAlive()) {
            breedingProbability = traits.lowBreedingProbability;
            for (WeatherType boostType : getBoostWeatherTypes()) {
                if (weather.getRecentWeather().contains(boostType)) {
                    breedingProbability = traits.highBreedingProbability;
                    break;
                }
            }
            grow();
            giveBirth(newPlants);
        }
    }

}
