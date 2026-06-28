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
     * Seek for the food in adjacent locations that are of type of plant.
     * 
     * @param preys The preys of the animal.
     * @return the location traveled to in order to access the food.
     */
    public Location findFood(List<Class<? extends Animal>> preys)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object obj = field.getObjectAt(where);
            if(obj instanceof Animal) {
                Animal animal = (Animal) obj;
                if(preys.contains(animal.getClass())) {
                    if(animal.isAlive()) {
                        animal.setDead();

                        this.foodLevel = animal.getFeedingValue();
                        return where;
                    }
                }
            }
        }
        return null;
    }

    /**
     * @return the set of animal types this carnivore preys upon.
     */
    protected abstract List<Class<? extends Animal>> getPreyDiet();

    /**
     * Carnivores feed by hunting the animals in their prey diet.
     *
     * @return the location moved into to feed, or null if no prey was found.
     */
    @Override
    protected Location seekFood()
    {
        return findFood(getPreyDiet());
    }
}
