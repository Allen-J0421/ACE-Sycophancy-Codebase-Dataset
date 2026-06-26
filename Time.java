/**
 * This class models time in the simulation. 
 * It stores details about time such as whether it is day or night,
 * the number of days the simulation has simulated through 
 * and the current time.
 *
 * @version 2022.03.2
*/
public class Time 
{
    private static final int HOURS_PER_DAY = 24;
    private static final int DAY_START_HOUR = 6;
    private static final int DAY_END_HOUR = 19;

    // to keep track of the flow of time
    private int stepCount;
    private int dayCount;
    private TimesOfDay dayOrNight;
    
    
    /**
     * Create a new instance of time with the step set to 0.
     */
    public Time() 
    {
        stepCount = 0;
        dayOrNight = TimesOfDay.NIGHT;
    }

    /**
     * Increments the time by 1. 
     * Additionally increments the day counter if
     * 24 hours have passed. 
     * Calls the method checkDayOrNight()
     */
    public void incrementTime()
    {
        advanceOneStep();
    }

    /**
     * Advances time by one simulation step.
     */
    public void advanceOneStep()
    {
        stepCount++;
        if(stepCount % HOURS_PER_DAY == 0){
            dayCount++;
        }
        checkDayOrNight();
    }

    /**
     * Checks the current hour and then sets 
     * time as day or night accordingly. 
     */
    public void checkDayOrNight()
    {
        if (getHour() >= DAY_START_HOUR && getHour() <= DAY_END_HOUR) {
            dayOrNight = TimesOfDay.DAY;
        }
        else {
            dayOrNight  = TimesOfDay.NIGHT;
        }
    }

    /**
     * Returns the current hour of the simulation. 
     */
    private int getHour() 
    {
        return stepCount % HOURS_PER_DAY; 
    }

    /**
     * Returns the current hour of the simulation 
     * as a string.
     */
    private String getHourString() 
    {
        int hour = getHour();
        if(hour < 10){
            return "0"+hour+":00";
        }
        return ""+hour+":00";
    }

    /**
     * Returns true if it is currently day time.
     */
    public boolean isDay() 
    {
        return dayOrNight==TimesOfDay.DAY;
    }

    /**
     * Returns the current time of the day.
     * Whether it is daytime or nighttime. 
     */
    public TimesOfDay getCurrentPeriod()
    {
        return dayOrNight;
    }

    /**
     * Returns the current time as a string.
     */
    public String getCurrentTimeString()
    {
        return " (" + getCurrentPeriod().toString() + ")" + " Day #" + dayCount + " " + getHourString();
    }

    /**
     * Returns the day count number. 
     */
    private int getDayCount()
    { 
        return dayCount; 
    }

    /**
     * Reset the time object
     */
    public void reset(){
        stepCount = 0;
        dayCount = 0;
        dayOrNight = TimesOfDay.NIGHT;
    }
}
