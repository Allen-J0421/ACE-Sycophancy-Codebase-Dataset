import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of an emu.
 * Emu age, move, eat grass, and die.
 *
 * @version 01.03.22
 */
public class Emu extends Animal
{
    // Characteristics shared by all emus (class variables).

    // The age at which an emu can start to breed.
    private static final int BREEDING_AGE = 30;
    // The age to which an emu can live.
    private static final int MAX_AGE = 600;
    // The likelihood of an emu breeding.
    private static final double BREEDING_PROBABILITY = 0.17;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 7;
    // The food value of a single grass. In effect, this is the
    // number of steps an emu can go before it has to eat again.
    private static final int GRASS_FOOD_VALUE = 60;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create an emu. An emu can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the emu will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Emu(boolean randomAge, Field field, Location location) {
        super(field, location);
        this.setGender();
        if(randomAge) {
            setAge(rand.nextInt(MAX_AGE));
            setFoodLevel(rand.nextInt(GRASS_FOOD_VALUE));
        }
        else {
            setAge(0);
           setFoodLevel(GRASS_FOOD_VALUE);
        }
    }

    /**
     * This is what the emu does most of the time: it eats grass.
     * In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newEmus A list to return newly born emus.
     * @param time the current time in the simulation
     */
    public void act(List<Animal> newEmus,int time) {
        incrementAge(MAX_AGE);
        incrementHunger();

        if(isAlive() && ((time <= 9)||(time >= 21))) {
            if (getDisease()){
                spreadDisease();
            }
            if (giveBirth(BREEDING_AGE)) {
                breedOffspring(newEmus, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
            }
            Location newLocation = findFood();
            if(newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            moveOrDie(newLocation);
        }
    }


    /**
     * Look for grass adjacent to the current location.
     * Only the first grass is eaten.
     * If acacia is adjacent, it is 'trampled'
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object searchPlant = field.getObjectAt(where);
            if(searchPlant instanceof Grass) {
                Grass grass = (Grass) searchPlant;
                if (grass.isAlive()) {
                    grass.setDead();
                    setFoodLevel(GRASS_FOOD_VALUE);
                    return where;
                }
            }
            else if (searchPlant instanceof Plant) {
                Plant plant = (Plant) searchPlant;
                if(plant.isAlive()) {
                    plant.setDead();
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Create a new emu offspring.
     */
    protected Animal createOffspring(Field field, Location location) {
        return new Emu(false, field, location);
    }
}
