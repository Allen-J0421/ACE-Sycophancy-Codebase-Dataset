package field;

/**
 * Represents an individual location in the Field.
 * It can contain an entity and have several attributes, such as 
 * temperature and water level.
 *
 */
public class Block {
    private int temperature;
    private int waterLevel;

    private Entity entity;

    /**
     * Constructor for Block
     * @param temperature the initial temperature of the block
     * @param waterLevel the intial water level of the block
     */
    public Block(int temperature, int waterLevel) {
        this.temperature = temperature;
        this.waterLevel = waterLevel;

        entity = null;
    }

    /* Setters */
    
    public void setEntity(Entity entity) { 
        this.entity = entity; 
    }

    /**
     * Sets the temperature of the block.
     * 
     * @param temperature should be between 0 and 70.
     */
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    /**
     * Sets the water level of the block.
     * 
     * @param waterLevel should not be negative.
     */
    public void setWaterLevel(int waterLevel) {
        this.waterLevel = waterLevel;
    }

    /* Getters */
    
    public Entity getEntity() { 
        return entity; 
    }

    public int getTemperature() {
        return temperature;
    }

    public int getWaterLevel() {
        return waterLevel;
    }
}
