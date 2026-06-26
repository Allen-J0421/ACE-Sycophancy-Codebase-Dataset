import java.util.List;
import java.lang.Math;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022/03/02
 */
public abstract class Animal extends Creature
{   
    // A shared random number generator to control aging, hunger and breeding.
    private static final Random rand = Randomizer.getRandom();

    // sex of an animal, 0 = female and 1 = male
    private int sex;
    // The animal's age.
    private int age;
    // The age to which this animal can live.
    private int maxAge;
    // The animal's food level.
    private int foodLevel;
    // The age at which this animal can start to breed.
    private int breedingAge;
    // The likelihood of this animal breeding.
    private double breedingProbability;
    // The maximum number of births.
    private int maxLitterSize;
    // Whether this animal needs a different-sex encounter to breed.
    private boolean requiresDifferentSexToBreed;

    // The amount of oxygen an animal need to survive
    protected static final double ANIMAL_OXYGEN_REQUIRED = 0.0000009;

    // If the animal is infected by disease.
    private boolean isInfected;
    // If the animal is immuned from the diease.
    private boolean isImmuned;

    // Track the first step at which the animal is infected;
    private int infectionStartStep;

    // total population that is die of disease.
    public static int populationDieOfDisease = 0;

    public Animal(boolean randomAge, Field field, Location location, int maxAge, int foodValue,
                  int breedingAge, double breedingProbability, int maxLitterSize,
                  boolean requiresDifferentSexToBreed){
        super(field, location);
        sex = (int)(Math.round(Math.random()));
        this.maxAge = maxAge;
        this.breedingAge = breedingAge;
        this.breedingProbability = breedingProbability;
        this.maxLitterSize = maxLitterSize;
        this.requiresDifferentSexToBreed = requiresDifferentSexToBreed;
        if(randomAge) {
            age = rand.nextInt(maxAge);
            foodLevel = rand.nextInt(foodValue);
        }
        else {
            age = 0;
            foodLevel = foodValue;
        }
        isInfected = false;
        isImmuned = false;
        // Track the first step at which the animal is infected;
        infectionStartStep = 0;

    }

    /**
     * This is what animals do most of the time: age, get hungry, breed,
     * search for food, move and consume oxygen.
     *
     * @param newAnimals A list to return newly born animals.
     * @param atDayTime true if current step is daytime false otherwise.
     * @param oxygenLevel The inital level of dissolved oxygen in the water.
     * @param disease The disease may happened during simulation.
     * @param step current step.
     *
     * @return the oxygen level the animal consumed after action.
     */
    public double act(List<Creature> newAnimals, boolean atDayTime, double oxygenLevel, Disease disease, int step)
    {
        if(oxygenLevel < ANIMAL_OXYGEN_REQUIRED){
            setDead();
            return 0;
        }

        // If the animal dies of disease, it will consume no oxygen.
        if(disease.progressInfection(this, step))
            return 0;

        incrementAge();
        incrementHunger();

        if(isAlive() && !needSleep(atDayTime)) {
            giveBirth(newAnimals);

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
        if(age > maxAge) {
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
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Animal young = createYoung(field, loc);
            newAnimals.add(young);
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
        if(canBreed() && rand.nextDouble() <= breedingProbability) {
            births = rand.nextInt(maxLitterSize) + 1;
        }
        return births;
    }

    /**
     * Check if this animal can breed.
     * @return true if this animal can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= breedingAge && (!requiresDifferentSexToBreed || encounterWithDiffSex());
    }

    /**
     * Look for food adjacent to the current location.
     * Only the first live food source is eaten. If a nearby animal is
     * infected, then this animal also may be infected.
     * @param disease disease.
     * @param step int current step.
     * @return Where food was found, or null if it wasn't.
     */
    public Location search(Disease disease, int step)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), 1);
        for(Location loc: adjacent) {
            Object creature = field.getObjectAt(loc);
            if(creature instanceof Animal) {
                Animal animal = (Animal) creature;
                if(animal.getIsInfected()) {
                    disease.expose(this, step);
                }
            }

            int foodValue = getFoodValue(creature);
            if(foodValue > 0 && creature instanceof Creature) {
                Creature food = (Creature) creature;
                if(food.isAlive()) {
                    food.setDead();
                    foodLevel = foodValue;
                    return loc;
                }
            }
        }
        return null;
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

    /**
     * Look for a nearby animal of the given class with a different sex.
     * @param animalClass The class of animal to find.
     * @param adjacentDistance Controls the search range.
     * @return true if a matching nearby animal has a different sex.
     */
    protected boolean encounterWithDiffSex(Class<? extends Animal> animalClass, int adjacentDistance)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(), adjacentDistance);
        for(Location where: adjacent) {
            Object animalAtThisLoc = field.getObjectAt(where);
            if(animalClass.isInstance(animalAtThisLoc)) {
                Animal animal = (Animal) animalAtThisLoc;
                if(this.getSex() != animal.getSex())
                    return true;
            }
        }
        return false;
    }

    /**
     * Create a new born animal of the same species.
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @return A new born animal.
     */
    protected abstract Animal createYoung(Field field, Location location);

    /**
     * Return the food value of the provided creature for this animal.
     * @param creature The adjacent creature to inspect.
     * @return the food value, or zero if it is not food.
     */
    protected abstract int getFoodValue(Object creature);

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

    boolean hasActiveInfection()
    {
        return isInfected && !isImmuned;
    }

    boolean isImmuneToDisease()
    {
        return isImmuned;
    }

    int getInfectionStartStep()
    {
        return infectionStartStep;
    }

    boolean infect(int step)
    {
        if(!isImmuned && !isInfected) {
            isInfected = true;
            infectionStartStep = step;
            return true;
        }
        return false;
    }

    void grantDiseaseImmunity()
    {
        isImmuned = true;
        isInfected = false;
    }

    void dieOfDisease()
    {
        setDead();
        populationDieOfDisease++;
    }

    
}
