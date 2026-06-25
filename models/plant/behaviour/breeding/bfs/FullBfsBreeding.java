package models.plant.behaviour.breeding.bfs;

import java.util.List;

import field.Actor;
import field.Location;
import models.plant.behaviour.breeding.BreedingBehaviour;

/**
 * BFS breeding, all adjacent Blocks available.
 *
 */
public class FullBfsBreeding extends BreedingBehaviour {

    BfsBreeding bfsBreeding;
    
    public FullBfsBreeding(int pRow, int pCol) {
        
        int[] dRow = new int[]{ 1, -1, 0, 0, 1, -1, 1, -1 };
        int[] dCol = new int[]{ 0, 0, 1, -1, 1, -1, -1, 1 };    

        bfsBreeding = new BfsBreeding(pRow, pCol, dRow, dCol);
    }

    @Override
    public Location act(List<Actor> newActors, double breeding_probability) {
        return bfsBreeding.act(newActors, breeding_probability);        
    }
}
