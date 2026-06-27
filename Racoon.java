import java.util.List;
import java.util.ArrayList;
import java.awt.Color;

import java.util.Random;

/**
 * A simple model of a Racoon.
 * Racoones age, move, eat rabbits, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Racoon extends Animal
{
    // Characteristics shared by all Racoones (class variables).
    

    /*
        Population Control variables
    */

    public static PopulationControls POPULATION_CONTROLS = new PopulationControls(
        15, //Breeding age 
        150, //Maxium age
        2, //Maximum litter size
        30, //Maximum hunger
        9, // Initial hunger
        0.26, //Breeding probability
        0.026 // creation probability
    );  
    // public static PopulationControls POPULATION_CONTROLS = new PopulationControls(
    //     15, //Breeding age 
    //     150, //Maxium age
    //     2, //Maximum litter size
    //     30, //Maximum hunger
    //     9, // Initial hunger
    //     0.2, //Breeding probability
    //     0.025 // creation probability
    // ); 


    //List of all animals that the Racoon eats
    private static final List<Class> EATS = new ArrayList<Class>(){{
        add(Mouse.class);
        add(Squirrel.class);
    }};

 

    /**
     * Create a Racoon. A Racoon can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Racoon will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Racoon(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(POPULATION_CONTROLS.getMaxAge());
            foodLevel = rand.nextInt(POPULATION_CONTROLS.getInitialHunger());
        } else {
            age = 0;
            foodLevel = POPULATION_CONTROLS.getInitialHunger();
        }
    }
    
    protected int getMaxAge() { return POPULATION_CONTROLS.getMaxAge(); }
    protected int getMaxHunger() { return POPULATION_CONTROLS.getMaxHunger(); }
    protected List<Class> getEats() { return EATS; }
    protected Simulatable createOffspring(Location loc) { return new Racoon(false, getField(), loc); }

    public Color getColor() {
        return Color.ORANGE;
    }

    public Color getBorderColor() {
        return getDisease() == null ? null : Color.PINK;
    }

    public Gender getGender () {
        return gender;
    }

    public int getBreedingAge () {
        return POPULATION_CONTROLS.getBreedingAge();
    }

    public int getAge () {
        return age;
    }

    public double getBreedingProbability() {
        return POPULATION_CONTROLS.getBreedingProbability();
    }

    public int getMaxLitterSize() {
        return POPULATION_CONTROLS.getMaxLitterSize();
    }
}
