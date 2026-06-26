import java.util.List;
import java.lang.Math;

/**
 * A class representing shared characteristics of animals.
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

    public Animal(Field field, Location location){
        super(field, location);
        sex = (int)(Math.round(Math.random()));
        isInfected = false;
        isImmuned = false;
        // Track the first step at which the animal is infected;
        infectionStartStep = 0;

    }

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

    protected abstract void incrementAge();

    protected abstract void incrementHunger();

    protected abstract void giveBirth(List<Creature> newAnimals);

    protected abstract void setFoodLevel(int foodLevel);

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
     *  Make an animal infected while the disease exists.
     *  
     *  @param disease disease 
     *  @param step current step.
     */
    protected void makeInfected(Disease disease, int step){
        if((!this.getIsImmuned()) && Randomizer.getRandom().nextDouble() <= disease.INFECTION_RATE) {
            infect(step);
        }
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
     * Find whether there is an adjacent animal of the same species but opposite sex.
     */
    protected boolean hasDifferentSexNearby(Class<? extends Animal> species, int searchDistance)
    {
        for(Location location : getField().adjacentLocations(getLocation(), searchDistance)) {
            Creature nearbyCreature = getField().getCreatureAt(location);
            if(species.isInstance(nearbyCreature)) {
                Animal nearbyAnimal = species.cast(nearbyCreature);
                if(this.getSex() != nearbyAnimal.getSex()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Search for prey around the animal and handle infection spread during the search.
     */
    @SafeVarargs
    protected final Location findFood(Disease disease, int step, int searchDistance, int foodValue,
                                      Class<? extends Creature>... preyTypes)
    {
        for(Location location : getField().adjacentLocations(getLocation(), searchDistance)) {
            Creature nearbyCreature = getField().getCreatureAt(location);
            exposeToInfection(nearbyCreature, disease, step);
            if(isMatchingPrey(nearbyCreature, preyTypes) && nearbyCreature.isAlive()) {
                nearbyCreature.setDead();
                setFoodLevel(foodValue);
                return location;
            }
        }
        return null;
    }

    /**
     * Move to a new location if possible, otherwise die from overcrowding.
     */
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

    @SafeVarargs
    private final boolean isMatchingPrey(Creature creature, Class<? extends Creature>... preyTypes)
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

    
}
