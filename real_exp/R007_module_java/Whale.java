import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a whale.
 * 
 * Whales age, move, eat cod or salmon, consume oxygen, propogate, 
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Whale extends Animal
{
    // Characteristics shared by all whales (class variables).

    // The age at which a whale can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a whale can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a whale breeding.
    private static final double BREEDING_PROBABILITY = 0.2;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 8;
    // The food value of a single Cod. In effect, this is the
    // number of steps a whale can go before it has to eat again.
    private static final int COD_FOOD_VALUE = 8;
    private static final int SALMON_FOOD_VALUE = 8;

    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The whale's age.
    private int age;
    // The whale's food level, which is increased by eating Cods.
    private int foodLevel;

    /**
     * Create a whale. A whale can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the whale will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Whale(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(COD_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = COD_FOOD_VALUE;
        }
    }

    /**
     * This is what the cod does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * 
     * @param newWhales A list to return newly born whales.
     * @param atDayTime true if current step is daytime false otherwise.
     * @param oxygenLevel The inital level of dissolved oxygen in the water.
     * @param disease The disease may happened during simulation.  
     * @param step current step.
     * 
     * @return the oxygen level the species produced or consumed after action.
     * 
     */
    public double act(List<Creature> newWhales, boolean atDayTime, double oxygenLevel, Disease disease, int step)
    {   
        if(oxygenLevel < ANIMAL_OXYGEN_REQUIRED){
            setDead();
            return 0;
        }     

        //if the whale dies of disease, it will consume no oxygen.
        if(dieOfInfection(disease))
            return 0;

        // check if the whale is qualified for immunity.
        ifCanGrantImmunity(disease, step);

        incrementAge();
        incrementHunger();

        if(isAlive() && !needSleep(atDayTime)) {
            giveBirth(newWhales);            
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
     * Increase the age. This could result in the whale's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this whale more hungry. This could result in the whale's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for Cods and Salmon adjacent to the current location.
     * Only the first live Cod or Salmon is eaten, if the nearby animal is 
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
            if(creature instanceof Cod || creature instanceof Salmon) {
                Animal animal = (Animal) creature;
                if(animal.isAlive()) { 
                    animal.setDead();
                    if(animal instanceof Cod)
                        foodLevel = COD_FOOD_VALUE;
                    else
                        foodLevel = SALMON_FOOD_VALUE;
                    return loc;
                }
            }
        }
        return null;

    }

    /**
     * Check whether or not this whale is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newwhales A list to return newly born whales.
     */
    private void giveBirth(List<Creature> newWhales)
    {
        // New whales are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Whale young = new Whale(false, field, loc);
            newWhales.add(young);
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
     * A whale can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * Decide whether two whales have different sex.
     * @return true if two whales have different sex, false otherwise.
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
