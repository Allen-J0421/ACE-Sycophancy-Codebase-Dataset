import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
/**
 * An extension of the Animal Class that can only eat a specified type of animals
 *
 * @version 1.0
 */
public abstract class CarnivoreAnimal extends Animal
{
    
    /*///////////////////////////////////////////////////////////////
                                CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/
    
    /**
     * Creates a Carnivore Animal.
     * 
     * @param randomAge Boolean flag dictating whether or not the attributes will be assigned a random value
     * @param field The environment the animal can move in
     * @param location Location of the animal in the field/grid
     * @param gender Gender of the animal
     * @param baseLevel the base hunger index associated with the animal
     * @param maxAge the maximum age an animal can take before he dies.
     */
    
    public CarnivoreAnimal(boolean randomAge,
                            Field field,
                            Location location,
                            Gender gender,
                            int baseLevel,
                            int maxLevel
                            )
    {
        super(randomAge,field, location, gender, baseLevel, maxLevel);
    }
    
    /**
     * @return the set of animal types this carnivore preys upon.
     */
    protected abstract List<Class<? extends Animal>> getPreyDiet();

    /**
     * Carnivores feed by hunting a live animal of their prey diet at the
     * given location.
     *
     * @param where the adjacent location to inspect.
     * @return true if prey was found and eaten.
     */
    @Override
    protected boolean eatAt(Location where)
    {
        Object obj = getField().getObjectAt(where);
        if(obj instanceof Animal) {
            Animal animal = (Animal) obj;
            if(getPreyDiet().contains(animal.getClass())) {
                if(animal.isAlive()) {
                    animal.setDead();

                    this.foodLevel = animal.getFeedingValue();
                    return true;
                }
            }
        }
        return false;
    }
}
