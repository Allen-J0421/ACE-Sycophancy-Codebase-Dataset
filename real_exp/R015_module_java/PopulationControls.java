
/**
 * Storage class containing the population controls shared by all animals 
 * 
 * @version 0.0.1
 */ 
class PopulationControls {

	private final int breedingAge, maxAge, maxLitterSize, maxFoodHunger, initialHunger;
	private final double breedingProbability, creationProbability;

	public PopulationControls(int breedingAge, int maxAge, int maxLitterSize, int maxFoodHunger, int initialHunger, double breedingProbability, double creationProbability) {
		//Using this to make it clearer which variables are being set. 
		this.breedingAge = breedingAge;
		this.maxAge = maxAge;
		this.maxLitterSize = maxLitterSize;
		this.maxFoodHunger = maxFoodHunger;
		this.initialHunger = initialHunger;
		this.breedingProbability = breedingProbability;
		this.creationProbability = creationProbability;

	} 


	/**
	 * @return breeding age
	 */ 
	public int getBreedingAge () {
		return breedingAge;
	}

	/**
	 * @return maximum age
	 */ 
	public int getMaxAge () {
		return maxAge;
	}

	/**
	 * @return the maximum litter size of an animal
	 */ 
	public int getMaxLitterSize () {
		return maxLitterSize;
	}


	/**
	 * @return the maxFoodHunger for an animal 
	 */
	public int getMaxHunger () {
		return maxFoodHunger;
	}

	/**
	 * @return the initial hunger
	 */
	public int getInitialHunger () {
		return initialHunger;
	}

	/**
	 * @return the breeding probability of an animal
	 */ 
	public double getBreedingProbability () {
		return breedingProbability;
	}


	/**
	 * @return the the probability with which an animal is created at the beginning of a sim. 
	 */ 
	public double getCreationProbability () {
		return creationProbability;
	}

}