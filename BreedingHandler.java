import java.lang.reflect.*;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
/**
 * Handles the breeding logic for animals, including finding compatible mates,
 * computing litter size, and placing newborns in the field.
 *
 * @version 1.0
 */
public class BreedingHandler
{

    /*///////////////////////////////////////////////////////////////
                               CONSTANTS
    //////////////////////////////////////////////////////////////*/

    private static final Random rand = Randomizer.getRandom();
    private static final Class[] ANIMAL_CONSTRUCTOR_SIGNATURE = new Class[] {boolean.class, Field.class, Location.class, Gender.class};

    /*///////////////////////////////////////////////////////////////
                                 STATE
    //////////////////////////////////////////////////////////////*/

    private final Field field;

    /*///////////////////////////////////////////////////////////////
                              CONSTRUCTOR
    //////////////////////////////////////////////////////////////*/

    /**
     * Creates a new BreedingHandler.
     *
     * @param field The field in which breeding takes place.
     */
    public BreedingHandler(Field field)
    {
        this.field = field;
    }

    /*///////////////////////////////////////////////////////////////
                           BREEDING LOGIC
    //////////////////////////////////////////////////////////////*/

    /**
     * Imitates the meeting of an animal by breeding new born animals.
     *
     * @param animal The animal attempting to breed.
     * @param newAnimals List to receive newly born animals.
     * @param maxLitter The maximum number of animals that can be born.
     * @param breedingProbability The likelihood of giving birth.
     * @param breedingAge The minimum age required to breed.
     */
    public void meet(Animal animal, List<Actor> newAnimals, int maxLitter, double breedingProbability, int breedingAge)
    {
        List<Location> adjacent = field.adjacentLocations(animal.getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object obj = field.getObjectAt(where);
            if(obj == null || !animal.getClass().equals(obj.getClass())) {
                continue;
            }
            Animal other = (Animal) obj;
            if(other.getGender() == animal.getGender()) {
                continue;
            }
            try
            {
                Constructor cons = animal.getClass().getConstructor(ANIMAL_CONSTRUCTOR_SIGNATURE);
                int births = breed(animal.getAge(), maxLitter, breedingProbability, breedingAge);
                giveBirth(animal, newAnimals, births, cons);
            }
            catch (NoSuchMethodException nsme)
            {
                nsme.printStackTrace();
            }
        }
    }

    /**
     * Returns the number of animals to breed.
     *
     * @param age The current age of the breeding animal.
     * @param maxLitter The maximum number of animals that can be born.
     * @param breedingProbability The likelihood of breeding.
     * @param breedingAge The minimum age required to breed.
     * @return The number of animals to give birth to.
     */
    private int breed(int age, int maxLitter, double breedingProbability, int breedingAge)
    {
        int births = 0;
        if(age >= breedingAge){
            double randomValue = rand.nextDouble();
            if(randomValue <= breedingProbability) {
                births = rand.nextInt(maxLitter) + 1;
            }
        }
        return births;
    }

    /**
     * Places newborn animals into adjacent free locations.
     *
     * @param animal The parent animal.
     * @param newAnimals List to receive newly born animals.
     * @param births The number of animals to give birth to.
     * @param cons The constructor to use when creating newborns.
     */
    private void giveBirth(Animal animal, List<Actor> newAnimals, int births, Constructor cons)
    {
        List<Location> free = field.getFreeAdjacentLocations(animal.getLocation());
        for (int i = 0; i < births && free.size() > 0; i++) {
            Location loc = free.remove(0);
            Gender randomGender = Utils.getRandomEnumValue(Gender.class);
            try {
                Object newObj = cons.newInstance(false, field, loc, randomGender);
                Animal newBorn = (Animal) newObj;
                newAnimals.add(newBorn);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
