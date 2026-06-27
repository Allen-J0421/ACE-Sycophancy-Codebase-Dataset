import java.util.List;
import java.util.Random;
import java.awt.Color;

/**
 * Class representing grass. 
 * Grass is eatable, simulatable and displayable 
 * 
 * @version 5.3.2
 */
class Grass extends Locatable implements Eatable, Simulatable, Displayable {


	/*
		Population Control variables
	*/
	public static final double CREATION_PROBABILITY = 0.05;
	public static final double BREEDING_PROBABILITY = 0.14;

	private boolean alive; //If the grass is alive
	private int rainAmount; // The amount of rain the grass has recived 

	private static final Random rand = Randomizer.getRandom();


	/**
	 * Initialiser. 
	 * 
	 * @param field the field on which the grass should exist
	 * @param location the location of the grass
	 */ 
	public Grass(Field field, Location location) {
		super(field, location);
		alive = true;
		rainAmount = 0;
	}

	/** 
	 * Implements act, required for all simulatable classes. 
	 * What the animal will do in a simulator cycle 
	 * 
	 * @param newGrass all grass that has been created
	 * @param currentState the simulators current state
	 */ 
	public void act(StepContext context) {
		if (context.getCurrentWeather().precipitates()) {
			rainAmount++;
		}
		if (isAlive()) {
			Grass newGrasslet = grow();
			if (newGrasslet != null) {
				context.spawn(newGrasslet);
			}
		}
	}


	/**
	 * Grow, defines how grass should grow
	 */ 
	private Grass grow() {
		Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());//Get adjacent location 
        for(Location i : free) {
        	//If probability is met, generate a new grasslet 
        	if (rand.nextDouble() <= getBreedingProbability(rainAmount)) {
        		rainAmount = 0;
        		Grass newGrasslet = new Grass(field, i);
	        	return newGrasslet;	
        	}
	        	
        }
        return null;
        
	}


	/**
	 * Implements displayable
	 * 
	 * @return the color of grass
	 */
	public Color getColor() {
        return Color.GREEN;
    }

    /**
     * Implmenets displayable
     * since grass cannot get a disease, it does not have a border color
     * @return null
     */
    public Color getBorderColor() {
        return null;
    }

    /**
     * Implements eatable
     * @return the food value of grass
     */
    public int getFoodValue(){
    	return 10;
    }


    /**
     * Implements simulatable 
     * will remove the grass from the field
     */
    public void setDead(){
    	removeFromField();
    	alive = false;
    }


    /**
     * Implements simulatable 
     * @return wether or not the animal is alive
     */
    public boolean isAlive() {
    	return alive;
    }

    /**
     * get the breeding probability. 
     * It is a transformed sigmoid function, which allows for the rain amount to be converted into a probabilty 
     * 0 is relativley little probability + the breeding probability, 10 is the maximum probability
     * @return the probability with which grass should breed
     */ 
    private double getBreedingProbability(int value) {

    	double bottom = 1 + Math.pow(Math.E, -((value / 2) - 2));
    	double rainProbability = (1 / bottom);
    	double allProbability = (5*BREEDING_PROBABILITY + rainProbability) / 2;
    	return BREEDING_PROBABILITY;
    }
}







