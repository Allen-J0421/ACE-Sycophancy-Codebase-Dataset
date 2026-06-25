import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2022.03.02
 */
public abstract class Animal extends Organism
{
    //the animal's gender true = male, false = female
    private boolean gender;
    // The animal's food level, which is increased by eating prey.
    protected int foodLevel;
    // The current time
    protected Time time = new Time();
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        super(field,location);
        Random rand = new Random();
        //Generate a random gender for the animal, true = male, false = female.
        if (rand.nextInt(2) == 0){
            gender = true;
        }
        else{
            gender = false;
        }
    }
    
    /**
     * Returns the animal's gender
     * @Return gender The animal's gender - True=male, False=female
     */
    protected boolean getGender()
    {
        return gender;
    }
    
    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Make this animal act during the day- that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract protected void actDay(List<Animal> newAnimals);
    
    /**
     * Make this animal act during during night - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */    
    abstract protected void actNight(List<Animal> newAnimals);
}


