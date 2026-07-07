import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a cod.
 * 
 * Cods age, move, eat seaweed, consume oxygen, propogate, 
 * and may get infected by disease and die of that or of weather.
 *
 * @version 2022/03/02
 */
public class Cod extends Animal
{
    // Characteristics shared by all cods (class variables).

    // The age at which a cod can start to breed.
    private static final int BREEDING_AGE = 6;
    // The age to which a cod can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a cod breeding.
    private static final double BREEDING_PROBABILITY = 0.3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 10;

    // The food value of a cod. In effect, this is the
    // number of steps a cod can go before it has to eat again.
    private static final int SEAWEED_FOOD_VALUE = 13;

    // Individual characteristics (instance fields).

    // THe cod's food level
    private int foodLevel;

    // Track the first step at which the animal is infected;
    private int infectionStartStep;

    /**
     * Create a new cod. A cod may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the cod will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Cod(boolean randomAge, Field field, Location location)
    {
        super(field, location);

        initAge(randomAge);
        if(randomAge) {
            foodLevel = rand.nextInt(SEAWEED_FOOD_VALUE);
        }
        else {
            foodLevel = SEAWEED_FOOD_VALUE;
        }
    }

    /**
     * This is what the cod does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * 
     * @param newCods A list to return newly born cods.
     * @param atDayTime true if current step is daytime false otherwise.
     * @param oxygenLevel The inital level of dissolved oxygen in the water.
     * @param disease The disease may happened during simulation.  
     * @param step current step.
     * 
     * @return the oxygen level the species produced or consumed after action.
     * 
     */
    public double act(List<Creature> newCods, boolean atDayTime, double oxygenLevel, Disease disease, int step)
    {   
        if(oxygenLevel < ANIMAL_OXYGEN_REQUIRED){
            setDead();
            return 0;
        }     

        //if the cod dies of disease, it will consume no oxygen.
        if(dieOfInfection(disease))
            return 0;

        // check if the cod is qualified for immunity.
        ifCanGrantImmunity(disease, step);

        incrementAge();
        incrementHunger();

        if(isAlive() && !needSleep(atDayTime)) {
            giveBirth(newCods);            
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
     * Make this cod more hungry. This could result in the cod's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    public boolean requiresMate() { return true; }
    public int getMaxAge() { return MAX_AGE; }
    public int getBreedingAge() { return BREEDING_AGE; }
    public double getBreedingProbability() { return BREEDING_PROBABILITY; }
    public int getMaxLitterSize() { return MAX_LITTER_SIZE; }
    public Animal createOffspring(Field field, Location location) { return new Cod(false, field, location); }

    /**
     * Decide whether two cods have different sex.
     * @return true if two cods have different sex, false otherwise.
     */
    public boolean encounterWithDiffSex(){
        Field field = getField();

        List<Location> adjacent = field.adjacentLocations(getLocation(), 1);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animalAtThisLoc = field.getObjectAt(where);
            if(animalAtThisLoc != null && animalAtThisLoc instanceof Cod){
                Cod codAtThisLoc = (Cod)animalAtThisLoc;
                if(this.getSex() != codAtThisLoc.getSex())
                    return true;
            }
        }
        return false;
    }

    /**
     * Look for seaweed adjacent to the current location.
     * Only the first live seaweed is eaten., if the nearby animal is 
     * infected, then this animal also may be infected.
     * @param disease disease.
     * @param step int step.
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

}
