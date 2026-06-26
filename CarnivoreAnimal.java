import java.util.Iterator;
import java.util.List;
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
     * Apply the standard carnivore step: age, hunger, breed, hunt prey, then move.
     *
     * @param newAnimals List of newborn animals.
     * @param maxAge Maximum age for this species.
     * @param maxLitterSize Maximum number of newborns per birth event.
     * @param breedingProbability Probability of breeding this step.
     * @param breedingAge Minimum age required for breeding.
     * @param preys Animal classes this species can eat.
     * @return true when prey was eaten during the step.
     */
    protected boolean actAsCarnivore(List<Actor> newAnimals,
                                     int maxAge,
                                     int maxLitterSize,
                                     double breedingProbability,
                                     int breedingAge,
                                     List<Class<? extends Animal>> preys)
    {
        if(!surviveStep(maxAge)) {
            return false;
        }

        meet(newAnimals, maxLitterSize, breedingProbability, breedingAge);
        Location foodLocation = findFood(preys);
        moveOrDie(foodLocation);
        return foodLocation != null;
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
}
