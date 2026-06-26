import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Abstract class for a prey in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Prey extends Animal implements Consumable {

    private static final double DEFAULT_ACTIVENESS = 1.0;
    private static final int SATIATION_THRESHOLD = 10;

    private int foodValue;
    private double activeness;

    // shared random generator to generate consistent results
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for a Prey in the simulation.
     *
     * @param traits    Species-level configuration for this prey.
     * @param foodValue The initial food value carried by this prey.
     * @param randomAge Whether we assign this prey a random age or not.
     * @param field     The field in which this prey resides.
     * @param location  The location in which this prey is spawned into.
     */
    public Prey(AnimalTraits traits, int foodValue, boolean randomAge, Field field, Location location) {
        super(traits, randomAge, field, location);
        this.foodValue  = foodValue;
        this.activeness = DEFAULT_ACTIVENESS;
    }

    /**
     * Returns the activeness multiplier applied during this prey's rest period.
     * Override in each concrete species with the species-specific fraction.
     *
     * @return A double in (0, 1] representing reduced activity.
     */
    abstract protected double getRestActiveness();

    /**
     * Performs one simulation step: ages, breeds, spreads disease, forages, and moves.
     * Dead prey linger on the field so scavengers can eat their corpses.
     */
    @Override
    public void act(List<Organism> newOrganisms, Weather weather, TimeOfDay time) {
        incrementAge();
        setActiveness(DEFAULT_ACTIVENESS);

        if (isAlive()) {
            giveBirth(newOrganisms);

            if (rand.nextDouble() <= getDeathByDiseaseProbability()) {
                remove();
                return;
            }

            if (time == getRestTime()) {
                setActiveness(getRestActiveness());
            }

            if (rand.nextDouble() <= getActiveness()) {
                Location newLocation = rand.nextDouble() <= getDiseaseSpreadProbability()
                    ? findAnimalToInfect() : findFood();

                if (newLocation == null || getFoodValue() > SATIATION_THRESHOLD) {
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }

                if (newLocation != null) {
                    setLocation(newLocation);
                } else {
                    remove();
                }
            }
        } else {
            decayifDead();
        }
    }

    /**
     * Searches adjacent cells for a living plant to eat.
     *
     * @return The location of the eaten plant, or null if none found.
     */
    @Override
    public Location findFood() {
        Field field = getField();
        Iterator<Location> it = field.adjacentLocations(getLocation()).iterator();
        while (it.hasNext()) {
            Location where = it.next();
            Organism organism = field.getObjectAt(where);
            if (organism instanceof Plant) {
                Plant plant = (Plant) organism;
                if (plant.isAlive()) {
                    plant.setDead();
                    return eat(plant) ? where : null;
                }
            }
        }
        return null;
    }

    /** Clears this prey from the field when it has been eaten. */
    @Override
    public void setEaten() {
        if (getLocation() != null) {
            getField().clear(getLocation());
            setLocationToNull();
            setField(null);
        }
    }

    @Override
    public int getFoodValue() { return foodValue; }

    protected void incrementFoodValue(int amount) { foodValue += amount; }

    /**
     * Eats a plant; infects this prey if the plant is poisonous.
     *
     * @param consumable The plant to be eaten.
     * @return true always (prey always eat what they find).
     */
    @Override
    public boolean eat(Consumable consumable) {
        if (consumable.isPoisonous() && !isInfected()) {
            infect(this);
        }
        incrementFoodValue(consumable.getFoodValue());
        consumable.setEaten();
        return true;
    }

    /** Prey are never poisonous. */
    @Override
    public boolean isPoisonous() { return false; }

    public double getActiveness()               { return activeness; }
    public void   setActiveness(double value)   { activeness = value; }
}
