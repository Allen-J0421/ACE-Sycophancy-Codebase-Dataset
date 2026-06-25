/**
 * Represent the time to distinct day and night.
 * Only present time in hours.
 *
 * @version 2022.2.22
 */
public class Time
{
    // Representation of time in hour, which is increased by clicking it.
    private int hour;
    // The distinction between day and night.
    private boolean day;

    /**
     * Create a time to count the hour of the day. 
     * The maxium hour of a day is 23 (0-23).
     * If startHour is greater than 24, start with the default hour 0.
     * @param startHour The start hour to count the time.
     */
    public Time(int startHour)
    {
        hour = startHour;
        isOneDay();
    }

    /**
     * @return The current hour of the day.
     */
    public int getTime()
    {
        return hour;
    }
    
    /**
     * The time will go for an hour if it is clicked.
     */
    public void timeClick()
    {
        hour = hour + 1;
        isOneDay();
    }
    
    /**
     * Distinct if the current time is day or night.
     * @return true if current time is day.
     * Day 6-17
     * Night 0-5 and 18-23
     */
    public boolean isDay()
    {
        day = true;
        if(getTime()>17 || getTime()<6) {
            day = !day;
        }
        return day;
    }
    
    /**
     * Set hour to the default vaule 0 if the current time is greater than a day.
     * @return The current hour.
     */
    private int isOneDay()
    {
        if(getTime() > 24) {
            hour = 0;
        }
        return hour; 
    }
}
