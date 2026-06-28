import java.util.List;
import java.util.Random;
import java.lang.Math;

/**
 * A class representing shared characteristics of animals.
 *
 * Animals age, move, eat, consume oxygen, propagate, and may get infected by a
 * disease and die of that or of weather. The common life-cycle (the {@link #act}
 * routine and its supporting steps) lives here; concrete species supply only
 * their own constants and the few behaviours that genuinely differ (what they
 * eat via {@link #search}, how they recognise a mate via
 * {@link #encounterWithDiffSex}, and how a newborn of their kind is created via
 * {@link #createChild}).
 *
 * @version 2022/03/02
 */
public abstract class Animal extends Creature
{

    // sex of an animal, 0 = female and 1 = male
    private int sex;

    // The amount of oxygen an animal need to survive
    protected static final double ANIMAL_OXYGEN_REQUIRED = 0.0000009;

    // If the animal is infected by disease.
    private boolean isInfected;
    // If the animal is immuned from the diease.
    private boolean isImmuned;

    // Track the first step at which the animal is infected;
    protected int infectionStartStep;

    // total population that is die of disease.
    public static int populationDieOfDisease = 0;

    // A shared random number generator to control breeding and ageing.
    protected static final Random rand = Randomizer.getRandom();

    // The animal's age.
    private int age;
    // The animal's food level, which is increased by eating.
    protected int foodLevel;

    /**
     * Create a new animal. An animal can be created as a new born (age zero and
     * full food level) or with a random age and food level.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param randomAge If true, the animal will have a random age and hunger level.
     */
    public Animal(Field field, Location location, boolean randomAge){
        super(field, location);
        sex = (int)(Math.round(Math.random()));
        isInfected = false;
        isImmuned = false;
        // Track the first step at which the animal is infected;
        infectionStartStep = 0;

        if(randomAge) {
            age = rand.nextInt(getMaxAge());
            foodLevel = rand.nextInt(getMaxFoodValue());
        }
        else {
            age = 0;
            foodLevel = getMaxFoodValue();
        }
    }

    /**
     * This is what an animal does most of the time - it moves around.
     * Sometimes it will breed, eat, or die of old age, hunger, disease,
     * suffocation or overcrowding.
     *
     * @param newAnimals A list to return newly born animals.
     * @param atDayTime true if current step is daytime false otherwise.
     * @param oxygenLevel The inital level of dissolved oxygen in the water.
     * @param disease The disease may happened during simulation.
     * @param step current step.
     *
     * @return the oxygen level the species produced or consumed after action.
     */
    public double act(List<Creature> newAnimals, boolean atDayTime, double oxygenLevel, Disease disease, int step)
    {
        if(oxygenLevel < ANIMAL_OXYGEN_REQUIRED){
            setDead();
            return 0;
        }

        //if the animal dies of disease, it will consume no oxygen.
        if(dieOfInfection(disease))
            return 0;

        // check if the animal is qualified for immunity.
        ifCanGrantImmunity(disease, step);

        incrementAge();
        incrementHunger();

        if(isAlive() && !needSleep(atDayTime)) {
            giveBirth(newAnimals);
            // Move towards a source of food if found.
            Location newLocation = search(disease, step);
            if(newLocation == null) {
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }

        return -ANIMAL_OXYGEN_REQUIRED;
    }

    /**
     * Increase the age. This could result in the animal's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     */
    private void giveBirth(List<Creature> newAnimals)
    {
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            newAnimals.add(createChild(field, loc));
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * An animal can breed if it has reached the breeding age and, for species
     * that require it, has encountered a mate of the opposite sex.
     * @return true if the animal can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= getBreedingAge() && (!requiresMate() || encounterWithDiffSex());
    }

    /**
     * @return The age to which this species can live.
     */
    protected abstract int getMaxAge();

    /**
     * @return The food value a newborn of this species starts with, and also
     *         the upper bound for a random starting food level.
     */
    protected abstract int getMaxFoodValue();

    /**
     * @return The age at which this species can start to breed.
     */
    protected abstract int getBreedingAge();

    /**
     * @return The likelihood of this species breeding in a given step.
     */
    protected abstract double getBreedingProbability();

    /**
     * @return The maximum number of births per breeding event.
     */
    protected abstract int getMaxLitterSize();

    /**
     * @return true if this species needs an adjacent mate of the opposite sex
     *         in order to breed, false if breeding age alone is enough.
     */
    protected abstract boolean requiresMate();

    /**
     * Create a newborn (age zero) animal of this species.
     * @param field The field to place the newborn in.
     * @param location The location for the newborn.
     * @return The newly created animal.
     */
    protected abstract Animal createChild(Field field, Location location);

    /**
     * get the gender of an animal.
     * @return sex  0 = female and 1 = male.
     */
    public int getSex(){
        return sex;
    }

    /**
     * Every animal have different gender. and the implementation of this method is at its subclass.
     *
     */
    public abstract boolean encounterWithDiffSex();

    public abstract Location search(Disease disease, int step);

    /**
     * identify whether a creature need to sleep
     *
     * @param atDayTime true if it is at day time false if it is at night time.
     * @return true if currently it is night.
     */
    public boolean needSleep(boolean atDayTime){
        return !atDayTime;
    }


    /**
     * get whether an animal is infected.
     *
     * @return true if an animal is infected, false otherwise.
     */
    public boolean getIsInfected(){
        return isInfected;
    }

    /**
     * get if an animal is immuned.
     *
     * @return true if an animal is immuned, false otherwise.
     */
    public boolean getIsImmuned(){
        return isImmuned;
    }

    /**
     * set an animal to be infected.
     * @param isInfected
     */
    public void setIsInfected(boolean isInfected){
        this.isInfected = isInfected;
    }

    /**
     * set an animal to be immuned.
     * @param isImmuned
     */
    public void setIsImmuned(boolean isImmuned){
        this.isImmuned = isImmuned;
    }

    /**
     *  Make an animal infected while the disease exists.
     *
     *  @param disease disease
     *  @param step current step.
     */
    protected void makeInfected(Disease disease, int step){
        if((!this.getIsImmuned()) && Randomizer.getRandom().nextDouble() <= disease.INFECTION_RATE)
            setIsInfected(true);

        //if the animal is infected in current step, record its start step.
        if(getIsInfected() && infectionStartStep == 0)
            infectionStartStep = step;
    }

    /**
     * give the animal immunity while condition is met.
     * @param disease disease.
     * @param step int step.
     */
    protected void ifCanGrantImmunity(Disease disease, int step){
        // if an animal is infected, it may die. Otherwise assume it gets immuntity from that disease.
        if(getIsInfected() && !getIsImmuned()){
            if(step-infectionStartStep >= disease.NUMBER_OF_STEP_TO_WITHSTAND){
                setIsImmuned(true);
                setIsInfected(false);
            }
        }
    }

    /**
     * set an animal to death if it is die of infection.
     * Return true if an animal dies of infection
     *
     * @return true if an animal dies of infection.
     */

    protected boolean dieOfInfection(Disease disease){
        // if an animal is infected, it may die. Otherwise assume it gets immuntity from that disease.
        if(getIsInfected() && !getIsImmuned()){
            if(Randomizer.getRandom().nextDouble() <= disease.MORTALITY_RATE  ){
                setDead();
                populationDieOfDisease++;
                return true;
            }
        }
        return false;
    }


}
