package models.plant.behaviour.breeding.bfs;

import java.util.List;

import field.Actor;
import field.Location;
import models.plant.behaviour.breeding.BreedingBehaviour;

/**
 * Logic for breeding using Breadth-First Search.
 *
 */
public class BfsBreeding extends BreedingBehaviour {
    private int[] dRow, dCol;
    private int pRow, pCol;

    public BfsBreeding(int pRow, int pCol, int[] dRow, int[] dCol) {
        this.dRow = dRow;
        this.dCol = dCol;

        this.pRow = pRow; 
        this.pCol = pCol;
    }

    /**
     * Check if the given location should be added to the queue
     * @param row row in the field
     * @param col column in the field
     * @return whether the given location should be added to the queue
     */
    private boolean isValid(int row, int col) {
        if (!field.inBounds(row, col))
            return false;

        if (field.getBlockAt(row, col).getEntity() != null) {
            return false;
        }
        
        return true;
    }

    /**
     * Compute the location where to add the new plant
     * @param newActors where to add the new plant
     * @param breeding_probability the breeding probability
     * @return Location the location where to add the new plant
     */
    @Override
    public Location act(List<Actor> newActors, double breeding_probability) {
        for (int i=0; i<dRow.length; i++) {
            int adj_row = pRow + dRow[i],
                adj_col = pCol + dCol[i];
            if (isValid(adj_row, adj_col)) {
                if (rand.nextDouble() > breeding_probability) {
                    return null;
                }
                Location loc = new Location(adj_row, adj_col);
                return loc;
            }
        }
        return null;
    }
}
