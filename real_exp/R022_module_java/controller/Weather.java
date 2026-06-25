package controller;

/**
 * Enumeration containing the possible weather state and their 
 * respective weight of being picked. Weights are relative.
 *
 */
public enum Weather {
    NORMAL(5), 
    SUNNY(15), 
    RAIN(80);

    private int weight;
    
    /**
     * Constructor for weather.
     * @param weight The weight of it being picked.
     */
    Weather(int weight) {
        this.weight = weight;
    }

    /**
     * Getter for weight.
     * @return weight of the weather.
     */
    public int getWeight() {
        return weight;
    }
}
