import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of an eagle.
 * Eagles age, move, eat snakes and rats, and die.
 *
 * @version 01.03.22
 */

public class Eagle extends Animal
{
    // Characteristics shared by all eagles (class variables).

    // The age at which an eagle can start to breed.
    private static final int BREEDING_AGE = 50;
    // The age to which an eagle can live.
    private static final int MAX_AGE = 700;
    // The likelihood of an  eagle breeding.
    private static final double BREEDING_PROBABILITY = 0.1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 11;
    // The food value of a single rat. In effect, this is the
    // number of steps an eagle can go before it has to eat again.
    private static final int RAT_FOOD_VALUE = 40;
    // The food value of a single snake. In effect, this is the
    // number of steps an eagle can go before it has to eat again.
    private static final int SNAKE_FOOD_VALUE = 60;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    /**
     * Create an eagle. An eagle can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Eagle(boolean randomAge, Field field, Location location) {
        super(field, location);
        initialise(randomAge);
    }

    protected int getMaxAge() { return MAX_AGE; }

    protected int getBreedingAge() { return BREEDING_AGE; }

    protected double getBreedingProbability() { return BREEDING_PROBABILITY; }

    protected int getMaxLitterSize() { return MAX_LITTER_SIZE; }

    protected int getInitialFoodLevel() { return SNAKE_FOOD_VALUE; }

    /**
     * Eagles are active through the daylight hours.
     */
    protected boolean isActive(int time) { return (time >= 6) && (time <= 22); }

    protected Animal createYoung(Field field, Location location) {
        return new Eagle(false, field, location);
    }

    /**
     * Look for rats and snakes adjacent to the current location.
     * Only the first live rat or snake is eaten.
     * If it is a plant, then it is 'trampled'
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood() {
        if (getFog() && (rand.nextInt(2) == 0)) {
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Object animal = field.getObjectAt(where);
                if(animal instanceof Snake) {
                    Snake snake = (Snake) animal;
                    if(snake.isAlive()) {
                        snake.setDead();
                        setFoodLevel(SNAKE_FOOD_VALUE);
                        return where;
                    }
                }
                else if(animal instanceof Rat) {
                    Rat rat = (Rat) animal;
                    if(rat.isAlive()) {
                        rat.setDead();
                        setFoodLevel(RAT_FOOD_VALUE);
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
        else {
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext()) {
                Location where = it.next();
                Object animal = field.getObjectAt(where);
                if (animal instanceof Snake) {
                    Snake snake = (Snake) animal;
                    if (snake.isAlive()) {
                        snake.setDead();
                        setFoodLevel(SNAKE_FOOD_VALUE);
                        return where;
                    }
                }
                else if(animal instanceof Rat) {
                    Rat rat = (Rat) animal;
                    if(rat.isAlive()) {
                        rat.setDead();
                        setFoodLevel(RAT_FOOD_VALUE);
                        return where;
                    }
                }
                else if (animal instanceof Plant){
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
}
