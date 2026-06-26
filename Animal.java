import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022/03/02
 */
public abstract class Animal extends Creature
{
    private static final Random RAND = Randomizer.getRandom();

    // The amount of oxygen an animal need to survive
    protected static final double ANIMAL_OXYGEN_REQUIRED = 0.0000009;

    private AnimalProfile profile;
    // sex of an animal, 0 = female and 1 = male
    private int sex;
    // If the animal is infected by disease.
    private boolean isInfected;
    // If the animal is immuned from the diease.
    private boolean isImmuned;
    // The animal's age.
    private int age;
    // The animal's food level.
    private int foodLevel;

    // Track the first step at which the animal is infected;
    protected int infectionStartStep;

    // total population that is die of disease.
    public static int populationDieOfDisease = 0;

    public Animal(boolean randomAge, Field field, Location location, AnimalProfile profile)
    {
        super(field, location);
        this.profile = profile;
        sex = RAND.nextBoolean() ? 1 : 0;
        isInfected = false;
        isImmuned = false;
        infectionStartStep = 0;
        initializeState(randomAge);
    }

    /**
     * get the gender of an animal.
     * @return sex  0 = female and 1 = male.
     */
    public int getSex()
    {
        return sex;
    }

    /**
     * identify whether a creature need to sleep
     *
     * @param atDayTime true if it is at day time false if it is at night time.
     * @return true if currently it is night.
     */
    public boolean needSleep(boolean atDayTime)
    {
        return !atDayTime;
    }

    /**
     * get whether an animal is infected.
     *
     * @return true if an animal is infected, false otherwise.
     */
    public boolean getIsInfected()
    {
        return isInfected;
    }

    /**
     * get if an animal is immuned.
     *
     * @return true if an animal is immuned, false otherwise.
     */
    public boolean getIsImmuned()
    {
        return isImmuned;
    }

    /**
     * set an animal to be infected.
     * @param isInfected infection state.
     */
    public void setIsInfected(boolean isInfected)
    {
        this.isInfected = isInfected;
    }

    /**
     * set an animal to be immuned.
     * @param isImmuned immunity state.
     */
    public void setIsImmuned(boolean isImmuned)
    {
        this.isImmuned = isImmuned;
    }

    protected int getAge()
    {
        return age;
    }

    /**
     * Run one simulation step for an animal.
     *
     * @return the oxygen level the species consumed after action.
     */
    public final double act(List<Creature> newAnimals, boolean atDayTime, double oxygenLevel,
                            Disease disease, int step)
    {
        if(oxygenLevel < ANIMAL_OXYGEN_REQUIRED) {
            setDead();
            return 0;
        }

        if(dieOfInfection(disease)) {
            return 0;
        }

        ifCanGrantImmunity(disease, step);
        incrementAge();
        incrementHunger();

        if(isAlive() && !needSleep(atDayTime)) {
            giveBirth(newAnimals);
            moveTo(search(disease, step));
        }

        return -ANIMAL_OXYGEN_REQUIRED;
    }

    /**
     * Make an animal infected while the disease exists.
     */
    protected void makeInfected(Disease disease, int step)
    {
        if(!getIsImmuned() && RAND.nextDouble() <= disease.INFECTION_RATE) {
            infect(step);
        }
    }

    /**
     * give the animal immunity while condition is met.
     */
    protected void ifCanGrantImmunity(Disease disease, int step)
    {
        if(getIsInfected() && !getIsImmuned()
           && step - infectionStartStep >= disease.NUMBER_OF_STEP_TO_WITHSTAND) {
            setIsImmuned(true);
            setIsInfected(false);
        }
    }

    /**
     * set an animal to death if it is die of infection.
     * Return true if an animal dies of infection
     */
    protected boolean dieOfInfection(Disease disease)
    {
        if(getIsInfected() && !getIsImmuned() && RAND.nextDouble() <= disease.MORTALITY_RATE) {
            setDead();
            populationDieOfDisease++;
            return true;
        }
        return false;
    }

    /**
     * Mark the animal as infected and record the first infected step.
     */
    public void infect(int step)
    {
        setIsInfected(true);
        if(infectionStartStep == 0) {
            infectionStartStep = step;
        }
    }

    /**
     * Decide whether a suitable mate is nearby.
     */
    public boolean encounterWithDiffSex()
    {
        return hasDifferentSexNearby(profile.getSpeciesClass(), profile.getMateSearchDistance());
    }

    /**
     * Search for configured prey around the animal.
     */
    public Location search(Disease disease, int step)
    {
        return findFood(disease, step, profile.getFoodValue(), profile.getPreyTypes());
    }

    /**
     * Increase the animal's age.
     */
    protected void incrementAge()
    {
        age++;
        if(age > profile.getMaxAge()) {
            setDead();
        }
    }

    /**
     * Reduce the animal's food level.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Create offspring into nearby free locations.
     */
    protected void giveBirth(List<Creature> newAnimals)
    {
        int births = breed();
        createAdjacentCreatures(newAnimals, births, profile::createYoung);
    }

    protected boolean canBreed()
    {
        boolean reachedBreedingAge = getAge() >= profile.getBreedingAge();
        return reachedBreedingAge && (!profile.requiresMate() || encounterWithDiffSex());
    }

    /**
     * Find whether there is an adjacent animal of the same species but opposite sex.
     */
    protected boolean hasDifferentSexNearby(Class<? extends Animal> species, int searchDistance)
    {
        for(Location location : getField().adjacentLocations(getLocation(), searchDistance)) {
            Creature nearbyCreature = getField().getCreatureAt(location);
            if(species.isInstance(nearbyCreature)) {
                Animal nearbyAnimal = species.cast(nearbyCreature);
                if(getSex() != nearbyAnimal.getSex()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Search for prey around the animal and handle infection spread during the search.
     */
    protected final Location findFood(Disease disease, int step, int foodValue,
                                      List<Class<? extends Creature>> preyTypes)
    {
        for(Location location : getField().adjacentLocations(getLocation(), 1)) {
            Creature nearbyCreature = getField().getCreatureAt(location);
            exposeToInfection(nearbyCreature, disease, step);
            if(isMatchingPrey(nearbyCreature, preyTypes) && nearbyCreature.isAlive()) {
                nearbyCreature.setDead();
                foodLevel = foodValue;
                return location;
            }
        }
        return null;
    }

    private void initializeState(boolean randomAge)
    {
        if(randomAge) {
            age = RAND.nextInt(profile.getMaxAge());
            foodLevel = RAND.nextInt(profile.getFoodValue());
        }
        else {
            age = 0;
            foodLevel = profile.getFoodValue();
        }
    }

    private void moveTo(Location preferredLocation)
    {
        Location newLocation = preferredLocation;
        if(newLocation == null) {
            newLocation = getField().freeAdjacentLocation(getLocation());
        }

        if(newLocation != null) {
            setLocation(newLocation);
        }
        else {
            setDead();
        }
    }

    private void exposeToInfection(Creature nearbyCreature, Disease disease, int step)
    {
        if(nearbyCreature instanceof Animal) {
            Animal nearbyAnimal = (Animal) nearbyCreature;
            if(nearbyAnimal.getIsInfected()) {
                makeInfected(disease, step);
            }
        }
    }

    private boolean isMatchingPrey(Creature creature, List<Class<? extends Creature>> preyTypes)
    {
        if(creature == null) {
            return false;
        }
        for(Class<? extends Creature> preyType : preyTypes) {
            if(preyType.isInstance(creature)) {
                return true;
            }
        }
        return false;
    }

    private int breed()
    {
        if(canBreed() && RAND.nextDouble() <= profile.getBreedingProbability()) {
            return RAND.nextInt(profile.getMaxLitterSize()) + 1;
        }
        return 0;
    }
}
