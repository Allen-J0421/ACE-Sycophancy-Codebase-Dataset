


/**
 * Breedable interface, enforces all functionaltiy requred for two different animals (or other) to breed
 * Also contains basic functionality which should be the same for all animlas (e.g male breeds with female)
 * @version 2.4.3
 */
interface Breedable {
	public Gender getGender();

	public int getBreedingAge(); 

	public int getAge();

	public double getBreedingProbability();

	public int getMaxLitterSize(); 


	/**
	 * Check if one Breedable can breed. Defualt implementation
	 * @param gender the gender of the animal
	 * @return boolean if that animal can breed. 
	 */
	default boolean canBreed(Gender gender) {
		return getAge() >= getBreedingAge() && getGender() == gender;
	}
}