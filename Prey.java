
/**
 * A class representing shared characteristics of prey.
 *
 * @version 15/03/2022
 */
public abstract class Prey extends Animal
{
    /**
     * Create a new prey at location in field.
     * 
     * @param randomAge If true, the prey will have a random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Prey(boolean randomAge, Field field, Location location){
        super(field, location, randomAge);
    }

    /**
     * Prey are always hungry.
     * @return true.
     */
    protected boolean isHungry(String animal)
    {
        return true;
    }
    
    /**
     * Returns the food of the prey if it eats plants.
     */
    abstract protected boolean isFood(Animal animal);
    
    /**
     * Prey do not sleep.
     * @return false.
     */
    protected boolean isAsleep()
    {
        return false;
    }
}

