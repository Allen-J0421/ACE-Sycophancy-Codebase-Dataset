import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a Salmon.
 * 
 * Salmons age, move, eat seaweed, consume oxygen, propogate,
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Salmon extends Animal
{
    // Characteristics shared by all salmons (class variables).

    // The age at which a salmon can start to breed.
    private static final int BREEDING_AGE = 4;
    // The age to which a salmon can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a salmon breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 15;

    // The food value of a single salmon. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int SEAWEED_FOOD_VALUE = 13;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).

    // The salmon's age.
    private int age;

    // The salmon's food level
    private int foodLevel;


    /**
     * Create a new salmon. A salmon may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the salmon will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Salmon(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(SEAWEED_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = SEAWEED_FOOD_VALUE;
        }
    }

    /**
     * This is what the salmon does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * 
     * @param newSalmons A list to return newly born salmons.
     * @param atDayTime true if current step is daytime false otherwise.
     * @param oxygenLevel The inital level of dissolved oxygen in the water.
     * @param disease The disease may happened during simulation.  
     * @param step current step.
     * 
     * @return the oxygen level the species produced or consumed after action.
     */
    public double act(List<Creature> newSalmons, boolean atDayTime, double oxygenLevel, Disease disease, int step)
    {
        // if there is not enough oxygen, set to death;
        if(oxygenLevel < ANIMAL_OXYGEN_REQUIRED){
            setDead();
            return 0;
        }     

        //if the salmon dies of disease, it will consume no oxygen.
        if(dieOfInfection(disease))
            return 0;

        // check if the salmon is qualified for immunity.
        ifCanGrantImmunity(disease, step);

        incrementAge();
        incrementHunger();

        if(isAlive() && !needSleep(atDayTime)) {
            giveBirth(newSalmons);            
            // Move towards a source of food if found.
            Location newLocation = search(disease, step);
            Field field = getField();

            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = field.freeAdjacentLocation(getLocation());
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
     * Increase the age.
     * This could result in the salmon's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this salmon more hungry. This could result in the salmon's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for seaweed adjacent to the current location.
     * Only the first live seaweed is eaten, if the nearby animal is 
     * infected, then this animal also may be infected.
     * @param disease disease.
     * @param step int current step.
     * @return Where food was found, or null if it wasn't.
     */
    public Location search(Disease disease, int step){
        Field field = getField();
         //trying to find food.
        List<Location> adjacent = field.adjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location loc = it.next();
            Object creature = field.getObjectAt(loc);
            //If nearby animal is infected,then it has the probability to be infected as well
            if(creature instanceof Animal){
                Animal animal = (Animal)creature;
                if(animal.getIsInfected()){
                    makeInfected(disease, step);
                }
            }
            // if food is found, set the food death.
            if(creature instanceof Seaweed) {
                Seaweed seaweed = (Seaweed) creature;
                if(seaweed.isAlive()) { 
                    seaweed.setDead();
                    foodLevel = SEAWEED_FOOD_VALUE;
                    return loc;
                }
            }
        }
        return null;

        
    }
    /**
     * Check whether or not this salmon is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newsalmons A list to return newly born salmons.
     */
    private void giveBirth(List<Creature> newSalmons)
    {
        // New salmons are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Salmon young = new Salmon(false, field, loc);
            newSalmons.add(young);
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
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A salmon can breed if it has reached the breeding age and has countered the salmon of opposite sex.
     * @return true if the salmon can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE && encounterWithDiffSex();
    }

    
    /**
     * Decide if two salmons countered has different sex;
     */
    public boolean encounterWithDiffSex(){

        List<Location> adjacentLocation = getField().adjacentLocations(getLocation(), 2);

        for(Location loc: adjacentLocation){
            Object creatureAtThisLoc = getField().getObjectAt(loc);
            if(creatureAtThisLoc != null && creatureAtThisLoc instanceof Salmon){
                Salmon salmonAtThisLoc = (Salmon)creatureAtThisLoc;
                if(this.getSex() != salmonAtThisLoc.getSex())
                    return true;
            }
        }
        return false;
    }

}
