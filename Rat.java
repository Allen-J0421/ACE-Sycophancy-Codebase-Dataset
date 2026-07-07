import java.util.Iterator;
import java.util.List;

/**
 * A simple model of a rat.
 * Rats age, move, eat ants, breed, and die.
 *
 * @version 01.03.22
 */
public class Rat extends Animal
{
    // The food value of a single ant. In effect, this is the
    // number of steps a rat can go before it has to eat again.
    private static final int ANT_FOOD_VALUE = 100;

    /**
     * Create a rat. A rat can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the rat will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rat(boolean randomAge, Field field, Location location) {
        super(field, location, SimulationConfig.RAT_CONFIG);
        initialise(randomAge);
    }

    /**
     * This is what the rat does most of the time: it hunts for
     * ants. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newRats A list to return newly born rats.
     * @param time the current time in the simulation
     */
    public void act(List<Animal> newRats, int time) {
        incrementAge(getMaxAge());
        incrementHunger();

        if(isAlive() && ((time >= 0)&&(time <= 18)))
        {
            if (getDisease()){
                spreadDisease();
            }
            spawnOffspring(newRats);
            moveOrDie();
        }
    }

    @Override protected Animal createOffspring(Field field, Location loc) { return new Rat(false, field, loc); }

    /**
     * Look for ants adjacent to the current location.
     * Only the first live ant is eaten.
     * if there is a plant adjacent, it can be 'trampled'
     * @return where food was found, or null if it wasn't.
     */
    @Override
    protected Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Ant) {
                Ant ant = (Ant) animal;
                if(ant.isAlive()) {
                    ant.setDead();
                    setFoodLevel(ANT_FOOD_VALUE);
                    return where;
                }
            }
            else if (animal instanceof Plant) {
                Plant plant = (Plant) animal;
                if(plant.isAlive()) {
                    plant.setDead();
                    return where;
                }
            }
        }
        return null;
    }
}
