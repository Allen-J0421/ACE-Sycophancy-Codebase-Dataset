import java.util.List;
import java.util.ArrayList;
import java.awt.Color;

import java.util.Random;

/**
 * model of a hawk
 * Foxes age, move, eat, and die.
 * 
 * @version 0.2.1
 */
public class Hawk extends Animal
{
    // Characteristics shared by all hawks.
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

    //An array containing what all Hawks eat (rats and squirrels)
    private static final List<Class> EATS = new ArrayList<Class>(){{
        add(Rat.class);
        add(Squirrel.class);
    }};

    


    /**
     * Create a Hawk. A Hawk can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Hawk will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Hawk(boolean randomAge, Field field, Location location)
    {
        super(field, location);//Initalise the locatable superaclass
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
    protected Simulatable createOffspring(Location loc) { return new Hawk(false, getField(), loc); }


    /**
     * Implements displayable
     * 
     * @return the color to display
     */
    public Color getColor() {
        return Color.MAGENTA;
    }


    /**
     * Implements displayable
     * 
     * @return the border color 
     */
    public Color getBorderColor() {
        return getDisease() == null ? null : Color.PINK;
    }


    /**
     * Implements breedable 
     * 
     * @return the gender of the animal
     */ 
    public Gender getGender () {
        return gender;
    }

    /** 
     * Implements breedable 
     * @return the breeding age of the animal
     */ 
    public int getBreedingAge () {
        return POPULATION_CONTROLS.getBreedingAge();
    }

    /**
     * implements breedable 
     * @return the age of the animal.
     */ 
    public int getAge () {
        return age;
    }

    /**
     * Implements breedable
     * @return the breeding probability of the animal 
     */ 
    public double getBreedingProbability() {
        return POPULATION_CONTROLS.getBreedingProbability();
    }

    /**
     * Implements breedable 
     * @return the max litter size of the animal 
     */ 
    public int getMaxLitterSize() {
        return POPULATION_CONTROLS.getMaxLitterSize();
    }

}
