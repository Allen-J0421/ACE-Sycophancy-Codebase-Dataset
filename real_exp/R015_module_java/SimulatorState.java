import java.util.Random;
import java.util.Map;

/**
 * This stores the current state of the simulator. All environmental factors affecting the players are stored in here
 * 
 * @version 13.7.6
 */
class SimulatorState {

	private double currentNormalisedTime = 0; //The normalised time is the time formatted as a probability 
	private Weather currentWeather = Weather.Sunny; //The weather of the sim defaulted to sunny 
	private Map<Class, Counter> counters;

	private static final double WEATHER_CHANGE_PROBABILITY = 0.01;  //The probability with which the weather changes
	private static final Random rand = Randomizer.getRandom();


	/**
	 * Set the normalised time of the state 
	 * @param time the normalised time
	 */ 
	public void setNormalisedTime(double time) {
		currentNormalisedTime = time;
	}

	/**
	 * This changes the current weather if it meets a probability. 
	 * It will not always do this 
	 */ 
	public void changeCurrentWeather() {
		if (rand.nextDouble() <= WEATHER_CHANGE_PROBABILITY) {
			int nextWeatherIndex = rand.nextInt(Weather.values().length);
		    setCurrentWeather(Weather.values()[nextWeatherIndex]);
		}
	}

	/**
	 * This forces a weather change and sets the weather. 
	 * @param weather the weather to set.
	 */
	public void setCurrentWeather(Weather weather){
		currentWeather = weather;
	}

	/**
	 * @return the current weather of the sim 
	 */ 
	public Weather getCurrentWeather() {
		return currentWeather;
	}


	/**
	 * @return the counters for all animals in the sim
	 */ 
	public Counter getCurrentStats(Class forAnimal) {
		return counters.get(forAnimal);
	}


	/**
	 * set the current population statists
	 * @param counter the counts
	 */ 
	public void setCurrentStats(Map<Class, Counter> counter) {
		counters = counter;
	}



	/**
	 * Aggragated probability reduction is all the rductions combined into one varaible. 
	 * This makes working out how all environmental factors will affect an animal easier. 
	 * @return the aggrated probabiltiy reduction
	 */
	public double getAggregatedProbabilityReduction() {
		return (currentNormalisedTime + getCurrentWeather().getReductionFactor()) / 2;
	}
}