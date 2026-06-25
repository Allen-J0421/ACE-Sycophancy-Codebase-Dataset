import java.util.List;

/**
 * A simple model of an owl.
 * Owls age, move during the day, eat frogs, breed and die.
 *
 * 
 * @version 2016.02.29 (2)
 */
public class Owl extends Animal
{
    // Characteristics shared by all owls (class variables).

    // The age at which a owl can start to breed.
    private static final int BREEDING_AGE = 10; //10
    // The age to which a owl can live.
    private static final int MAX_AGE = 135; //80
    // The likelihood of a owl breeding.
    private static final double BREEDING_PROBABILITY = 0.3;    //0.08
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;   //1
    // The food value of a single prey. In effect, this is the
    // number of steps a owl can go before it has to eat again.
    private static final int FOOD_REQUIREMENT_TIME = 18; 

    // Individual characteristics (instance fields).

    /**
     * Create a Owl. A owl can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the owl will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Owl(boolean randomAge, Field field, Location location)
    {
        super(randomAge, field, location, MAX_AGE, FOOD_REQUIREMENT_TIME);
        foodTypes = new Class[]{Frog.class, Insect.class};
        speed = 3;
        maxAge = MAX_AGE;
    }

    /**
     * Create a Owl. A owl can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the owl will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Species spawn(boolean randomAge, Field field, Location location)
    {
        return new Owl(randomAge, field, location);
    }

    /**
     * This is what the owl does most of the time: it hunts for
     * frogs. In the process, it might breed, die of hunger,
     * or die of old age.
     * 
     * Owls only move and breed at night, and sleep during the day.
     * 
     * @param field The field currently occupied.
     * @param newOwl A list to return newly born owls.
     */
    public void act(List<Species> newOwl, boolean isDay)
    {
        incrementAge();
        incrementHunger();
        if (!isDay){
            if(isAlive()) {
                diseaseInfect();
                giveBirth(newOwl, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);            
                Weather weather = getField().getWeather();
                if(weather != Weather.RAIN){
                    findFoodAndMove(FOOD_REQUIREMENT_TIME);
                }
            }
        }
    }
}
