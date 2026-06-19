public final class AnimalTraits {

	private final int breedingAge;
	private final int maxAge;
	private final double breedingProbability;
	private final int maxLitterSize;
	private final int foodChainLevel;
	private final int foodValue;
	private final int sickProbability;
	private final int recoverProbability;


	public AnimalTraits(int breedingAge,
						int maxAge,
						double breedingProbability,
						int maxLitterSize,
						int foodChainLevel,
						int foodValue,
						int sickProbability,
						int recoverProbability) {
		this.breedingAge = breedingAge;
		this.maxAge = maxAge;
		this.breedingProbability = breedingProbability;
		this.maxLitterSize = maxLitterSize;
		this.foodChainLevel = foodChainLevel;
		this.foodValue = foodValue;
		this.sickProbability = sickProbability;
		this.recoverProbability = recoverProbability;
	}


	public int getBreedingAge() {
		return breedingAge;
	}


	public int getMaxAge() {
		return maxAge;
	}


	public double getBreedingProbability() {
		return breedingProbability;
	}


	public int getMaxLitterSize() {
		return maxLitterSize;
	}


	public int getFoodChainLevel() {
		return foodChainLevel;
	}


	public int getFoodValue() {
		return foodValue;
	}


	public int getSickProbability() {
		return sickProbability;
	}


	public int getRecoverProbability() {
		return recoverProbability;
	}
}
