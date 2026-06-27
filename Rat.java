import java.util.List;
import java.util.ArrayList;

import java.util.Random;
import java.awt.Color;

/**
 * A simple model of a rat.
 * Rats age, move, breed, and die.
 * 
 * @version 0.0.1
 */
public class Rat extends Animal implements Eatable
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
		0.27, // Breeding probability
		0.08// Creation probability
	); 


	
	private static final int FOOD_VALUE = 9;//This is the food value of the rat (how much it will feed its preadtor) 

	//An array containing what all Rats eat
	private static final List<Class> EATS = new ArrayList<Class>(){{
		add(Grass.class);
	}};


	

	/**
	 * Create a new rat. A rat may be created with age
	 * zero (a new born) or with a random age.
	 * 
	 * @param randomAge If true, the rat will have a random age.
	 * @param field The field currently occupied.
	 * @param location The location within the field.
	 */
	public Rat(boolean randomAge, Field field, Location location)
	{
		super(field, location);//Initalise the super class
		age = 0;
		foodLevel = POPULATION_CONTROLS.getInitialHunger();
		if(randomAge) {
			age = rand.nextInt(POPULATION_CONTROLS.getMaxAge());
		}

	}


	protected int getMaxAge() { return POPULATION_CONTROLS.getMaxAge(); }
	protected int getMaxHunger() { return POPULATION_CONTROLS.getMaxHunger(); }
	protected List<Class> getEats() { return EATS; }
	protected Simulatable createOffspring(Location loc) { return new Rat(false, getField(), loc); }
	
	/**
	 * Implementation of eatable 
	 * @return the food value of a rat
	 */
	public int getFoodValue (){
		return FOOD_VALUE;
	}

	/**
	 * implementation of displayable 
	 * @return the color of a rat
	 */ 
	public Color getColor() {
		return Color.YELLOW;
	}

	/**
	 * Implementaion of displayable 
	 * @return the border color of a rat
	 */
	public Color getBorderColor() {
		return getDisease() == null ? null : Color.PINK;
	}

	/**
	 * Implementaion of breedable
	 * @return the age at which rats can start breeding 
	 */
	public int getBreedingAge () {
		return POPULATION_CONTROLS.getBreedingAge();
	}

	/**
	 * Implementaion of breedable
	 * @return the age of this rat
	 */
	public int getAge () {
		return age;
	}

	/**
	 * Implementaion of breedable
	 * @return the breeding probability of rats
	 */
	public double getBreedingProbability() {
		return POPULATION_CONTROLS.getBreedingProbability();
	}


	/**
	 * Implementaion of breedable
	 * @return the maximum litter size of rats
	 */
	public int getMaxLitterSize() {
		return POPULATION_CONTROLS.getMaxLitterSize();
	}
}