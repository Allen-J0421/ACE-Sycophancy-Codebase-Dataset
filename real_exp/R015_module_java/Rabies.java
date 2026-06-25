

/**
 * Rabies class, a type of disease animals can get 
 * 
 * @version 0.8.0
 */
class Rabies implements Disease {

	// The creation probability for all rrabies viruses 
	public static double CREATION_PROBABILITY = 0.01;


	/**
	 * Implementation of disease
	 * @return the effect the disease will ahve on the hunger 
	 */
	public int getHungerEffect() {
		return 1;
	}

	/**
	 * Implementation of disease 
	 * @return the effect rabies has on the infected animal
	 */
	public int getAgeEffect(){
		return 2;
	}

	/**
	 * Implementation of disease
	 * @return the rate at which the disease should spread
	 */ 
	public double getR0() {
		return 0.05; 
	}


}