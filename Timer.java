import java.util.Random;

/**
 * Sets the time based on the number of steps in the simulator.
 *
 * @version 2022.03.01
 */
public class Timer
{
    private String currentTime;
    private String minutes;
    private static final Random random = Randomizer.getRandom();
    
    /**
     * Generates a random time between 6 and 11am.
     */
    public String getMorningTime(){
        return getRandomHour(6);
    }

    /**
     * Generates a random time between 12 and 5pm.
     */
    public String getAfternoonTime(){
        return getRandomHour(12);
    }

    /**
     * Generates a random time between 6 and 11pm.
     */
    public String getEveningTime(){
        return getRandomHour(18);
    }

    /**
     * Generates a random time between 12 and 5am.
     */
    public String getNightTime(){
        return getRandomHour(0);
    }

    /**
     * Generate an hour within a six-hour period.
     * @param startHour The first hour in the period.
     * @return The generated hour as a string.
     */
    private String getRandomHour(int startHour){
        currentTime = Integer.toString(random.nextInt(6) + startHour);
        return currentTime;
    }

    /**
     * Adds ":" to the current time string + minutes
     * @return the current time string with ':' + minutes.
     */
    public String toString(){
        return currentTime +":" + minutes;
    }
    
    /**
     * Randomly generates minutes.
     * @return minutes as a string.
     */
    public String getMinutes(){
        int min = random.nextInt(60);
        minutes = formatMinutes(min);
        return minutes;
    }

    /**
     * Format minute values with a leading zero when needed.
     * @param min The minute value.
     * @return The formatted minutes.
     */
    private String formatMinutes(int min){
        if(min < 10){
            return "0" + min;
        }
        return Integer.toString(min);
    }

    /**
     * Uses the step number to decide what time of day it is to randomly generate an apporopriate time.
     * @param steps. The step number currently in the simulation.
     * @return Current time in the simualtion.
     */
    public String getTime(int steps){
        int value = steps % 4;
        switch(value) {
            case 0:
                getMorningTime();
                break;
            case 1:
                getAfternoonTime();
                break;
            case 2:
                getEveningTime();
                break;
            case 3:
                getNightTime();
                break;
            default:
                break;
        }
        getMinutes();
        return toString();
    }
}
