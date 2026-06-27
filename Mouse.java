import java.util.List;
import java.util.ArrayList;

import java.util.Random;
import java.awt.Color;

/**
 * A simple model of a mouse.
 * Mouses age, move, breed, and die.
 *
 * @version 2016.02.29 (2)
 */
public class Mouse extends Animal implements Eatable
{
	// Characteristics shared by all mouses (class variables).

	/*
		Population Control variables
	*/
	public static PopulationControls POPULATION_CONTROLS = new PopulationControls(
		5, //Breeding age
		40, // Maximum age
		4, // Maximum litter size
		50, //Maximum hunger (how full a mouse can be)
		20, // Initial hunger
		0.36, // Breeding probability
		0.08// Creation probability
	); 

	// public static PopulationControls POPULATION_CONTROLS = new PopulationControls(
	// 	5, //Breeding age
	// 	40, // Maximum age
	// 	4, // Maximum litter size
	// 	50, //Maximum hunger (how full a mouse can be)
	// 	20, // Initial hunger
	// 	0.12, // Breeding probability
	// 	0.2// Creation probability
	// ); 

	private static final int FOOD_VALUE = 9;//This is the food value of the mouse (how much it will feed its preadtor) 


	//An array containing what all Mouses
	private static final List<Class> EATS = new ArrayList<Class>(){{
		add(Grass.class);
	}};


	/**
	 * Create a new mouse. A mouse may be created with age
	 * zero (a new born) or with a random age.
	 * 
	 * @param randomAge If true, the mouse will have a random age.
	 * @param field The field currently occupied.
	 * @param location The location within the field.
	 */
	public Mouse(boolean randomAge, Field field, Location location)
	{
		super(field, location);
		age = 0;
		foodLevel = POPULATION_CONTROLS.getInitialHunger();
		if(randomAge) {
			age = rand.nextInt(POPULATION_CONTROLS.getMaxAge());
		}

	}
	
	protected int getMaxAge() { return POPULATION_CONTROLS.getMaxAge(); }
	protected int getMaxHunger() { return POPULATION_CONTROLS.getMaxHunger(); }
	protected List<Class> getEats() { return EATS; }
	protected Simulatable createOffspring(Location loc) { return new Mouse(false, getField(), loc); }


	/**
	 * implementing eatable
	 * @return the food value of the mouse 
	 */
	public int getFoodValue (){
		return FOOD_VALUE;
	}

	/** 
	 * Implementing displayable 
	 * @return the color of the mouse
	 */ 
	public Color getColor() {
		return Color.BLUE;
	}


	/**
	 * implementing displayable
	 * @return if the animal is diseased, return a pink color, else return null
	 */
	public Color getBorderColor() {
		return getDisease() == null ? null : Color.PINK;
	}



	/**
	 * Implementing breedable
	 * @return the breeding age of the animal
	 */
	public int getBreedingAge () {
		return POPULATION_CONTROLS.getBreedingAge();
	}

	/**
	 * Implementing breedable
	 * @return the age of the animal
	 */
	public int getAge () {
		return age;
	}

	/**
	 * Implementing breedable
	 * @return the breeding probability of the animal
	 */
	public double getBreedingProbability() {
		return POPULATION_CONTROLS.getBreedingProbability();
	}


	/**
	 * Implementing breedable
	 * @return the maximum litter size of the animal 
	 */
	public int getMaxLitterSize() {
		return POPULATION_CONTROLS.getMaxLitterSize();
	}
}
