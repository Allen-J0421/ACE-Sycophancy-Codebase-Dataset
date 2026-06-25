
/**
 * Interface enforcing the required functionality of a disease
 * @version 1.3.2
 */
interface Disease {

	/**
	 * Should return the effect the disease has on the victims hunger
	 */
	public int getHungerEffect() ; 

	/**
	 * Should return how much the disease shortens an animlas life 
	 */
	public int getAgeEffect() ;

	/**
	 * Should return the probability which an animal will get a disease 
	 */
	public double getR0();

}