

/**
 * Weather enum represents all weathers that the simulator has. 
 * It also shows the probability reductions that a particular weather has on the players
 * 
 * @version 8.6.5
 */

enum Weather {
	Rainy(0.5),
	Sunny(1),
	Cloudy(0.85),
	Stormy(0.2);

	private double reductionFactor; 


	/**
	 * @return the probability reduction that a weather has on the players
	 */
	public double getReductionFactor() {
        return this.reductionFactor;
    }
  
    /**
     * Constructor for a weather 
     * @param reductionFactor the probability reduction that a weather has on the players
     */
    private Weather(double reductionFactor) {
        this.reductionFactor = reductionFactor;
    }

}