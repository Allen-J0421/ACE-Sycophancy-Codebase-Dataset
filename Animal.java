import java.util.List;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022/03/02
 */
public abstract class Animal extends Creature
{
    // sex of an animal, 0 = female and 1 = male
    private int sex;

    // The amount of oxygen an animal needs to survive.
    protected static final double ANIMAL_OXYGEN_REQUIRED = 0.0000009;

    private boolean infected;
    private boolean immune;
    private int infectionStartStep;

    private static int diseaseDeathCount = 0;

    // Age and food level shared by all animal subclasses.
    protected int age;
    protected int foodLevel;

    public Animal(Field field, Location location) {
        super(field, location);
        sex = Randomizer.getRandom().nextInt(2);
        infected = false;
        immune = false;
        infectionStartStep = 0;
    }

    // -----------------------------------------------------------------------
    // Abstract methods — subclasses supply species-specific values and types.
    // -----------------------------------------------------------------------

    protected abstract int getBreedingAge();
    protected abstract int getMaxAge();
    protected abstract double getBreedingProbability();
    protected abstract int getMaxLitterSize();

    /** Factory method: return a newborn of this species placed at location. */
    protected abstract Animal createYoung(Field field, Location location);

    /**
     * Look for food adjacent to the current location.
     * Herbivores eat seaweed; carnivores hunt prey animals.
     * Also spreads disease from infected neighbours encountered during the search.
     */
    public abstract Location search(Disease disease, int step);

    // -----------------------------------------------------------------------
    // Template act() — identical lifecycle for every animal species.
    // -----------------------------------------------------------------------

    /**
     * Core lifecycle: check oxygen, handle disease, age, hunger, breed, move.
     *
     * @return oxygen consumed (negative) or 0 if the animal died this step.
     */
    public double act(List<Creature> newAnimals, boolean atDayTime, double oxygenLevel, Disease disease, int step) {
        if(oxygenLevel < ANIMAL_OXYGEN_REQUIRED) {
            setDead();
            return 0;
        }
        if(dieOfInfection(disease)) return 0;
        ifCanGrantImmunity(disease, step);
        incrementAge();
        incrementHunger();
        if(!isAlive()) return 0;
        if(isAwake(atDayTime)) {
            giveBirth(newAnimals);
            Location newLocation = search(disease, step);
            if(newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            if(newLocation != null) {
                setLocation(newLocation);
            } else {
                setDead();
            }
        }
        return -ANIMAL_OXYGEN_REQUIRED;
    }

    // -----------------------------------------------------------------------
    // Shared breeding and lifecycle helpers.
    // -----------------------------------------------------------------------

    /** Increase age; die on reaching MAX_AGE. */
    protected void incrementAge() {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }

    /** Decrease food level; die on starvation. */
    protected void incrementHunger() {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /** True if old enough and a mate of the same species and opposite sex is nearby. */
    private boolean canBreed() {
        return age >= getBreedingAge() && hasMateNearby();
    }

    /** Return the number of offspring produced this step (may be zero). */
    private int breed() {
        int births = 0;
        if(canBreed() && Randomizer.getRandom().nextDouble() <= getBreedingProbability()) {
            births = Randomizer.getRandom().nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /** Place offspring of this species in adjacent free locations. */
    private void giveBirth(List<Creature> newAnimals) {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            newAnimals.add(createYoung(field, loc));
        }
    }

    /**
     * True if an animal of the same species but opposite sex is within radius 2.
     * Uses getClass() so each species only matches its own kind.
     */
    private boolean hasMateNearby() {
        List<Location> adjacent = getField().adjacentLocations(getLocation(), 2);
        for(Location loc : adjacent) {
            Object obj = getField().getObjectAt(loc);
            if(obj != null && obj.getClass() == this.getClass()) {
                Animal other = (Animal) obj;
                if(this.getSex() != other.getSex()) return true;
            }
        }
        return false;
    }

    // -----------------------------------------------------------------------
    // Disease mechanics.
    // -----------------------------------------------------------------------

    /**
     * Force this animal into the infected state, recording when the infection began.
     * Used by Disease during an outbreak; bypasses the immunity probability check.
     */
    protected void infect(int step) {
        setInfected(true);
        if(infectionStartStep == 0) {
            infectionStartStep = step;
        }
    }

    /**
     * Probabilistically infect this animal via contact with a diseased neighbour.
     * Has no effect if the animal is already immune.
     */
    protected void makeInfected(Disease disease, int step) {
        if(!isImmune() && Randomizer.getRandom().nextDouble() <= disease.INFECTION_RATE)
            setInfected(true);
        if(isInfected() && infectionStartStep == 0)
            infectionStartStep = step;
    }

    /**
     * Grant immunity once the animal has withstood the disease long enough.
     */
    protected void ifCanGrantImmunity(Disease disease, int step) {
        if(isInfected() && !isImmune()) {
            if(step - infectionStartStep >= disease.STEPS_TO_IMMUNITY) {
                setImmune(true);
                setInfected(false);
            }
        }
    }

    /**
     * Kill the animal if it succumbs to infection this step.
     * @return true if the animal died of infection.
     */
    protected boolean dieOfInfection(Disease disease) {
        if(isInfected() && !isImmune()) {
            if(Randomizer.getRandom().nextDouble() <= disease.MORTALITY_RATE) {
                setDead();
                diseaseDeathCount++;
                return true;
            }
        }
        return false;
    }

    /** Return the total number of animals that have died of disease in this run. */
    public static int getDiseaseDeathCount() { return diseaseDeathCount; }

    /** Reset the disease-death counter (call when the simulation resets). */
    public static void resetDiseaseDeathCount() { diseaseDeathCount = 0; }

    // -----------------------------------------------------------------------
    // Accessors.
    // -----------------------------------------------------------------------

    /** Return the gender of this animal (0 = female, 1 = male). */
    public int getSex() { return sex; }

    /**
     * Return true if this animal is awake and able to act.
     * By default animals are diurnal: awake during the day, asleep at night.
     * Subclasses can override this for nocturnal behaviour.
     */
    public boolean isAwake(boolean atDayTime) { return atDayTime; }

    public boolean isInfected() { return infected; }
    public boolean isImmune()   { return immune;   }

    public void setInfected(boolean infected) { this.infected = infected; }
    public void setImmune(boolean immune)     { this.immune   = immune;   }
}
