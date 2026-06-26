import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An abstract animal organism present in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Animal extends Organism implements AbleToEat {

    private final AnimalTraits traits;
    private final Gender gender;
    private boolean infected;

    // shared random generator to generate consistent results
    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for an animal in the simulation.
     *
     * @param traits    Species-level configuration for this animal.
     * @param randomAge Whether the animal should have a random age or not.
     * @param field     The field in which the animal resides.
     * @param location  The location in which the animal spawns into.
     */
    public Animal(AnimalTraits traits, boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);
        this.traits = traits;
        this.gender = Gender.getRandom();
        this.infected = false;
    }

    // ── Organism stat getters (satisfied here so subclasses need not override) ──

    @Override public double getBreedingProbability()       { return traits.breedingProbability; }
    @Override public int    getMaxLitterSize()             { return traits.maxLitterSize; }
    @Override public int    getMaxAge()                    { return traits.maxAge; }
    @Override public int    getBreedingAge()               { return traits.breedingAge; }

    // ── Disease stat getters ───────────────────────────────────────────────────

    protected double getDiseaseSpreadProbability()         { return traits.diseaseSpreadProbability; }
    protected double getDeathByDiseaseProbability()        { return traits.deathByDiseaseProbability; }

    // ── Rest behaviour (common to Predator, Scavenger, and Prey) ──────────────

    /**
     * Returns the time of day at which this animal is inactive.
     * Provided by traits so concrete subclasses need not override it.
     */
    protected TimeOfDay getRestTime()                      { return traits.restTime; }

    // ── Breeding ───────────────────────────────────────────────────────────────

    /**
     * Checks adjacent locations for an animal of the same species and
     * opposite gender.
     *
     * @return Whether this animal can breed or not.
     */
    @Override
    protected boolean canBreed() {
        if (getAge() < getBreedingAge()) return false;
        for (Location loc : getField().adjacentLocations(getLocation())) {
            Object obj = getField().getObjectAt(loc);
            if (obj != null && obj.getClass() == getClass()) {
                Animal other = (Animal) obj;
                if (isMale() != other.isMale()) return true;
            }
        }
        return false;
    }

    // ── Gender ─────────────────────────────────────────────────────────────────

    protected boolean isMale() { return gender == Gender.MALE; }

    // ── Disease ────────────────────────────────────────────────────────────────

    protected boolean isInfected() { return infected; }

    protected void infect(Animal animal) { animal.infected = true; }

    /**
     * If this animal is infected, attempts to infect a healthy animal in
     * an adjacent cell.
     *
     * @return The location of the targeted neighbour, or null if none found.
     */
    protected Location findAnimalToInfect() {
        if (!infected) return null;
        for (Location loc : getField().adjacentLocations(getLocation())) {
            Object organism = getField().getObjectAt(loc);
            if (organism instanceof Animal) {
                Animal animal = (Animal) organism;
                if (animal.isAlive() && !animal.isInfected()) {
                    if (rand.nextDouble() <= 0.01) {
                        infect(animal);
                    }
                    return loc;
                }
            }
        }
        return null;
    }
}
