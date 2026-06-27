import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends Organism
{
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // The animal's hurt level, when > 3 die
    private int burn;
    // The animal's gender.
    private final Gender gender;

    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        super(field, location);
        burn = 0;
        gender = Gender.random(rand);
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @param step The current step.
     * @param weather The current weather.
     */
    abstract public void act(List<Animal> newAnimals, int step, Weather weather);
    
    /**
     * update the animal's burn status.
     * @param weather The current weather.
     */
    protected void updateBurnStatus(Weather weather)
    {
        if (burn > 0) {
            if (weather != Weather.RAINY) {
                burn();
            }
            else {
                recover();
            }
        }
    }
    
    /**
     * animal that step on fire will get burn and if burn status > 3 the animal will die 
     */
    protected void burn()
    {
        burn++;
        if (burn > 3) {
            setDead();
        }
    }
    
    /**
     * if weather is rainy the animal will recover and reset it's burn status to zero.
     */
    protected void recover()
    {
        burn = 0;
    }

    /**
     * @return the animal's gender
     */
    protected final Gender getGender()
    {
        return gender;
    }
}
