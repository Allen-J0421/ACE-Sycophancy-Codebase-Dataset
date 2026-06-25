
import java.util.List;
import java.util.ArrayList;

import java.util.Random;
import java.awt.Color;

/**
 * A simple model of a squirel.
 * Squirrels age, move, breed, and die.
 * 
 * 
 * @version 0.0.3
 */
public class Squirrel extends Animal implements Eatable
{

    /*
        Population Control variables
    */
    public static PopulationControls POPULATION_CONTROLS = new PopulationControls(
        5, //Breeding age
        40, // Maximum age
        4, // Maximum litter size
        50, //Maximum hunger (how full a mouse can be)
        20, // Initial hunger
        0.34, // Breeding probability
        0.1// Creation probability
    ); 


    
    private static final int FOOD_VALUE = 10;// The amount which a squirll fills up the predators


    // A list of all classes a squirl eats
    private static final List<Class> EATS = new ArrayList<Class>(){{
        add(Grass.class);
    }};


    /**
     * Create a Squrril. A Squrril can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Squrril will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Squirrel(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        foodLevel = POPULATION_CONTROLS.getInitialHunger();
        if(randomAge) {
            age = rand.nextInt(POPULATION_CONTROLS.getMaxAge());
        }

    }


    /**
     * This is what the squirrel does most of the time. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newAnimals A list to return newly born foxes.
     */
    public void act(List<Simulatable> newAnimals, SimulatorState currentState)
    {
        incrementAge(POPULATION_CONTROLS.getMaxAge());
        Field f = getField();
        if(rand.nextDouble() <= currentState.getAggregatedProbabilityReduction()) {
            incrementHunger();
            if (isAlive()) {
                giveNearbyAnimalsDisease();

                Counter numberOfAnimal = currentState.getCurrentStats(Squirrel.class);
                giveBirth(numberOfAnimal, Squirrel.class, newAnimals, (Location loc) -> new Squirrel(false, f, loc));

                Disease currentDisease = getDisease();
                if (currentDisease != null) {
                    foodLevel -= currentDisease.getHungerEffect();
                    age -= currentDisease.getAgeEffect();
                }

                moveToNewLocation(EATS, POPULATION_CONTROLS.getMaxHunger());
            }
        }
    }


    /**
     * Implementation of eatable 
     * @return the food value of a squirrel
     */
    public int getFoodValue (){
        return FOOD_VALUE;
    }

    /**
     * implementation of displayable 
     * @return the color of a squirrel
     */ 
    public Color getColor() {
        return Color.RED;
    }



    /**
     * Implementaion of displayable 
     * @return the border color of a squirrel
     */
    public Color getBorderColor() {
        return getDisease() == null ? null : Color.PINK;
    }


    /**
     * Implementaion of breedable
     * @return the age at which squirrels can start breeding 
     */
    public int getBreedingAge () {
        return POPULATION_CONTROLS.getBreedingAge();
    }

    /**
     * Implementaion of breedable
     * @return the age of this squirrel
     */
    public int getAge () {
        return age;
    }


    /**
     * Implementaion of breedable
     * @return the breeding probability of squirrels
     */
    public double getBreedingProbability() {
        return POPULATION_CONTROLS.getBreedingProbability();
    }

    /**
     * Implementaion of breedable
     * @return the maximum litter size of squirrels
     */
    public int getMaxLitterSize() {
        return POPULATION_CONTROLS.getMaxLitterSize();
    }
}