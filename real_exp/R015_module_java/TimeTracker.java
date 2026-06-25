


/**
 * Time tracker, a class which is used to keep track of time in the sim. 
 * It is also used to calculate the normalised time which is the time as a probability. 
 * This probability is the effect that a time has on the players. 
 * 
 * It stores time as a 24 hour variable where the int value is the hours and the decimal value is the minutes
 * 
 * @version 15.4.9
 */
class TimeTracker {

	private double time; 

	private static final double DEFAULT_INCREMENT = 0.25;//The amount which a simulator cycle increases the time


	/**
	 * Time tracker initalised with a start time
	 * @param startTime the time at which the tracker starts 
	 */
	public TimeTracker(double startTime ) {
		time = startTime;
	}

	/**
	 * Time tracker initalised with a time of zero
	 */
	public TimeTracker() {
		time = 0;
	}


	/**
	 * Increment the time. IF the time is larger then 24 it will wrap around to the beginning. 
	 */ 
	public void incrementTime(double byValue) {
		time = (time + byValue) % 24;
	}


	/**
	 * Increment the time by the defualt increment. 
	 */
	public void incrementTime() {
		incrementTime(DEFAULT_INCREMENT);
	}


	/**
	 * Get the time as a double. 
	 * 
	 * @return the time
	 */
	public double getTime(){ 
		return time;
	}


	/** 
	 * Return the time as a string. Is displayed as a 24 hour time. 
	 * 
	 * @return string time
	 */
	public String getPrettyTime(){

		double minutes = (time - Math.floor(time)) * 60;
		String formattedMinutes = String.valueOf((int)minutes);
		formattedMinutes = (minutes < 10) ? "0" + formattedMinutes : formattedMinutes;

		return String.valueOf((int)Math.floor(time)) + ":" + formattedMinutes;
	}


	/**
	 * Force the time to change to a particular value. useful for a reset to set the time to zero. 
	 * @param newTime the time to set to.
	 */
	public void setTime(double newTime) {
		time = newTime;
	}


	/**
	 * Normalised time. Used a transformed cosine curve. This is because it oscilates with its max (or min with a - at the beginning) at zero. 
	 * Since the night is darkest at 12:00 (in this sim in real life it is different) this equation works perfectly. 
	 * 
	 * @return the time normalised as a probability. 
	 */
	public double normalisedTime(){
		double value = -Math.cos(time * (Math.PI / 12) );
		double normalisedForPercentage = 0.5 * (value + 1);
		return normalisedForPercentage;
	}

}