/**
 * A generic class that holds and manipulates the temperature level.
 *
 * @version 2022.03.01
 */
public class Thermometer
{
    // the current temperature
    private int currentTemp;

    /**
     * Construct a Thermometer object with the given temperature.
     *
     * @param initialTemp (int) The initial temperature.
     */
    public Thermometer(int initialTemp)
    {
        currentTemp = initialTemp;
    }

    /**
     * @return (int) The current temperature.
     */
    public int getTemperature()
    {
        return currentTemp;
    }

    /**
     * Set the temperature to the given parameter.
     *
     * @param temp (int) The new temperature value.
     */
    public void setTemperature(int temp)
    {
        currentTemp = temp;
    }

    /**
     * Increment the current temperature by the given parameter.
     *
     * @param inc (int) The amount of the increment.
     */
    public void incrementTemperature(int inc)
    {
        currentTemp += inc;
    }
}
