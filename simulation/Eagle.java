package simulation;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import configuration.Configuration;

/**
 * A simple model of an eagle.
 * Eagles age, move, eat snakes and rats, and die.
 *
 * @version 01.03.22
 */

public class Eagle extends Animal
{
    private static final Configuration.AnimalTuning TUNING = Configuration.defaults().species().eagle();
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
    public Eagle(boolean randomAge, SimulationContext context, Location location) {
        super(context, location);
        this.setGender();
        if(randomAge) {
            setAge(rand.nextInt(TUNING.getMaxAge()));
            setFoodLevel(rand.nextInt(TUNING.getNewbornFoodLevel()));
        }
        else {
            setAge(0);
            setFoodLevel(TUNING.getNewbornFoodLevel());
        }
    }

    /**
     * This is what the eagle does most of the time: it hunts for
     * rats and snakes. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param newEagles A list to return newly born eagles.
     * @param time the current time in the simulation
     */
    public void act(int time) {
        incrementAge(TUNING.getMaxAge());
        incrementHunger();
        if(isAlive() && TUNING.isActive(time)) {
            if (getDisease()){
                spreadDisease();
            }
            if (giveBirth(TUNING.getBreedingAge())) {
                breedOffspring(TUNING.getBreedingAge(),
                        TUNING.getBreedingProbability(), TUNING.getMaxLitterSize());
            }
            Location newLocation = findFood();
            if(newLocation == null) {
                newLocation = getContext().freeAdjacentLocation(getLocation());
            }
            moveOrDie(newLocation);

        }

    }

    /**
     * Look for rats and snakes adjacent to the current location.
     * Only the first live rat or snake is eaten.
     * If it is a plant, then it is 'trampled'
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood() {
        if (getFog() && (rand.nextInt(2) == 0)) {
            SimulationContext context = getContext();
            List<Location> adjacent = context.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Object animal = context.getObjectAt(where);
                if(animal instanceof Snake) {
                    Snake snake = (Snake) animal;
                    if(snake.isAlive()) {
                        snake.setDead();
                        context.emit(new FoodConsumptionEvent(this, this, snake,
                                TUNING.foodValueFor(Configuration.SpeciesId.SNAKE)));
                        setFoodLevel(TUNING.foodValueFor(Configuration.SpeciesId.SNAKE));
                        return where;
                    }
                }
                else if(animal instanceof Rat) {
                    Rat rat = (Rat) animal;
                    if(rat.isAlive()) {
                        rat.setDead();
                        context.emit(new FoodConsumptionEvent(this, this, rat,
                                TUNING.foodValueFor(Configuration.SpeciesId.RAT)));
                        setFoodLevel(TUNING.foodValueFor(Configuration.SpeciesId.RAT));
                        return where;
                    }
                }
                else if (animal instanceof Plant) {
                    Plant plant = (Plant) animal;
                    if(plant.isAlive()) {
                        plant.setDead();
                        context.emit(new FoodConsumptionEvent(this, this, plant, 0));
                        return where;
                    }
                }
            }
            return null;
        }
        else {
            SimulationContext context = getContext();
            List<Location> adjacent = context.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while (it.hasNext()) {
                Location where = it.next();
                Object animal = context.getObjectAt(where);
                if (animal instanceof Snake) {
                    Snake snake = (Snake) animal;
                    if (snake.isAlive()) {
                        snake.setDead();
                        context.emit(new FoodConsumptionEvent(this, this, snake,
                                TUNING.foodValueFor(Configuration.SpeciesId.SNAKE)));
                        setFoodLevel(TUNING.foodValueFor(Configuration.SpeciesId.SNAKE));
                        return where;
                    }
                }
                else if(animal instanceof Rat) {
                    Rat rat = (Rat) animal;
                    if(rat.isAlive()) {
                        rat.setDead();
                        context.emit(new FoodConsumptionEvent(this, this, rat,
                                TUNING.foodValueFor(Configuration.SpeciesId.RAT)));
                        setFoodLevel(TUNING.foodValueFor(Configuration.SpeciesId.RAT));
                        return where;
                    }
                }
                else if (animal instanceof Plant){
                    Plant plant = (Plant) animal;
                    if(plant.isAlive()) {
                        plant.setDead();
                        context.emit(new FoodConsumptionEvent(this, this, plant, 0));
                        return where;
                    }
                }
            }
            return null;
        }
    }

    /**
     * Create a new eagle offspring.
     */
    protected Animal createOffspring(SimulationContext context, Location location) {
        return new Eagle(false, context, location);
    }
}
