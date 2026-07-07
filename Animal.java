import java.util.List;
import java.util.Iterator;
import java.util.Random;
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
    // The animal's current age.
    private int age;
    // The animal's current food level.
    private int foodLevel;
    // A shared random number generator.
    protected static final Random rand = Randomizer.getRandom();

    // The amount of oxygen an animal need to survive
    protected static final double ANIMAL_OXYGEN_REQUIRED = 0.0000009;
    // The possibility that an animal may be infected by a disease.
    protected static final double INFECTION_RATE = 1;
    // The possibility an animal may die of a disease.
    protected static final double MORTALITY_RATE = 1;
    // The steps an animal need to withstand in order to get immunity
    protected static final int stepStandNum = 3;

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

    protected void initAge(boolean randomAge) {
        age = randomAge ? rand.nextInt(getMaxAge()) : 0;
    }

    protected void initFoodLevel(boolean randomAge) {
        foodLevel = randomAge ? rand.nextInt(getMaxFoodValue()) : getMaxFoodValue();
    }

    protected int getAge() { return age; }

    public abstract int getMaxAge();
    public abstract int getBreedingAge();
    public abstract double getBreedingProbability();
    public abstract int getMaxLitterSize();
    public abstract int getMaxFoodValue();
    public abstract Animal createOffspring(Field field, Location location);
    protected abstract int tryEat(Object creature);

    public boolean requiresMate() { return false; }

    protected boolean canBreed() {
        return age >= getBreedingAge() && (!requiresMate() || encounterWithDiffSex());
    }

    protected int breed() {
        int births = 0;
        if (canBreed() && rand.nextDouble() <= getBreedingProbability()) {
            births = rand.nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    protected void incrementAge() {
        age++;
        if (age > getMaxAge()) {
            setDead();
        }
    }

    protected void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            setDead();
        }
    }

    protected void giveBirth(List<Creature> newAnimals) {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            newAnimals.add(createOffspring(field, loc));
        }
    }

    public double act(List<Creature> newAnimals, boolean atDayTime, double oxygenLevel, Disease disease, int step) {
        if (oxygenLevel < ANIMAL_OXYGEN_REQUIRED) {
            setDead();
            return 0;
        }
        if (dieOfInfection(disease)) return 0;
        ifCanGrantImmunity(disease, step);
        incrementAge();
        incrementHunger();
        if (isAlive() && !needSleep(atDayTime)) {
            giveBirth(newAnimals);
            Location newLocation = search(disease, step);
            if (newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            if (newLocation != null) {
                setLocation(newLocation);
            } else {
                setDead();
            }
        }
        return -ANIMAL_OXYGEN_REQUIRED;
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

    public Location search(Disease disease, int step) {
        List<Location> adjacent = getField().adjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();
        while (it.hasNext()) {
            Location loc = it.next();
            Object creature = getField().getObjectAt(loc);
            if (creature instanceof Animal) {
                Animal animal = (Animal) creature;
                if (animal.getIsInfected()) makeInfected(disease, step);
            }
            int gained = tryEat(creature);
            if (gained >= 0) {
                foodLevel = gained;
                return loc;
            }
        }
        return null;
    }

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
