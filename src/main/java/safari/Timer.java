package safari;

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
     * Constructor for objects of class Timer
     */
    public Timer()
    {
        
    }
    
    /**
     * Generates a random time between 6 and 11am.
     */
    public String getMorningTime(){
        currentTime = Integer.toString(random.nextInt(6) + 6);
        return currentTime;
    }

    /**
     * Generates a random time between 12 and 5pm.
     */
    public String getAfternoonTime(){
        currentTime = Integer.toString(random.nextInt(6) + 12);
        return currentTime;
    }

    /**
     * Generates a random time between 6 and 11pm.
     */
    public String getEveningTime(){
        currentTime = Integer.toString(random.nextInt(6) + 18);
        return currentTime;
    }

    /**
     * Generates a random time between 12 and 5am.
     */
    public String getNightTime(){
        currentTime =Integer.toString(random.nextInt(6));
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
        minutes = Integer.toString(min);
        if(min < 10){
            minutes = "0" + minutes;
        }
        return minutes;
    }

    /**
     * Uses the step number to decide what time of day it is to randomly generate an apporopriate time.
     * @param steps. The step number currently in the simulation.
     * @return Current time in the simualtion.
     */
    public String getTime(int steps){
        int value = steps % 4;
        if(value == 0){
            currentTime =  getMorningTime();
        } else if(value == 1){
            currentTime = getAfternoonTime();
        } else if(value == 2){
            currentTime = getEveningTime();
        } else if(value == 3){
            currentTime = getNightTime();
        }
        getMinutes();
        return toString();
    }
}

