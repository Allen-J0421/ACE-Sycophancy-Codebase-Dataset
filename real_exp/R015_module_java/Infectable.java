

/**
 * Interface enforcing the required functionality for an animal which can get diseases. 
 */
interface Infectable {


	/**
	 * Should return the disease that the animal currently has
	 * Will return null if the animal does not have a disease
	 */ 
	public Disease getDisease () ;

}