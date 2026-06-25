/**
 * A generic class that represents a season
 * of the year
 *
 * @version 2022.02.16
 */
public class  Season
{
    private final String name;
    private int aveTemperature;
    private final int tempChange;
    // store the current temperature of this season
    private Thermometer currentTemp;

    /**
     * Initialise the Season object
     *
     * @param name (String) the name of the season
     * @param aveTemperature (int) the average temperature of this season
     * @param tempChange (int) the amount that the season's temperature can go up or down from the average temperature
     */
    public Season(String name, int aveTemperature, int tempChange)
    {
        this.name = name;
        this.aveTemperature = aveTemperature;
        this.tempChange = tempChange;

        currentTemp = new Thermometer(aveTemperature);
    }

    /**
     * @return (String) the name of the season
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return (int) average Temperature
     */
    public int getAveTemperature()
    {
        return aveTemperature;
    }

    /**
     * Increment the season's average temperature by the given parameter
     *
     * @param inc (int) the increment
     */
    public void incAveTemperature(int inc)
    {
        aveTemperature += inc;
    }

    /**
     * @return (int) The temperature change.
     */
    public int getTempChange()
    {
        return tempChange;
    }

    /**
     * @return (int) The upper limit temperature that the season could reach
     */
    public int getUpperLimitTemp()
    {
        return aveTemperature + tempChange;
    }

    /**
     * @return (int) The lower limit temperature that the season could drop to
     */
    public int getLowerLimitTemp()
    {
        return aveTemperature - tempChange;
    }

    /**
     * @return (Thermometer) A reference to the Thermometer object of this season
     */
    public Thermometer getCurrentTemp()
    {
        return currentTemp;
    }

}
