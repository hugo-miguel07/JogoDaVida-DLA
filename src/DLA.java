import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public class DLA implements IProcessingApp 
{
    private int NUM_WALKERS = 100;
    private int NUM_STEPS_PER_FRAME = 10;
    private int cellSize = 5;
    private int nrows, ncols;
    
    private List<Walker> wanderingWalkers;
    private int[][] grid; // 0 = empty, >0 = aggregation order
    private int aggregationCounter;
    
    @Override
    public void setup(PApplet parent) 
    {
        parent.noStroke();
        
        ncols = parent.width / cellSize;
        nrows = parent.height / cellSize;
        
        grid = new int[nrows][ncols];
        wanderingWalkers = new ArrayList<Walker>();
        aggregationCounter = 0;
        
        // Create the seed particle at the center
        int centerRow = nrows / 2;
        int centerCol = ncols / 2;
        aggregationCounter++;
        grid[centerRow][centerCol] = aggregationCounter;
        

        
        // Create initial wandering particles
        for (int i = 0; i < NUM_WALKERS; i++)
        {
            Walker w = new Walker(parent, cellSize, nrows, ncols);
            wanderingWalkers.add(w);
        }
    }

    @Override
    public void draw(PApplet parent, float dt) 
    {
        parent.background(200);

        List<Walker> toRemove = new ArrayList<Walker>();
        
        for (int i = 0; i < NUM_STEPS_PER_FRAME; i++)
        {
            for (Walker w : wanderingWalkers) {
                w.wander(parent);
                w.updateState(parent, grid);
                
                // Check if walker just stopped
                if (w.getState() == Walker.State.STOPPED) {
                    aggregationCounter++;
                    
                    // Mark cell as occupied in grid with aggregation order
                    grid[w.getRow()][w.getCol()] = aggregationCounter;
                    toRemove.add(w);
                }
            }
            
            // Remove stopped walkers from wandering list
            wanderingWalkers.removeAll(toRemove);
            
            // Respawn new walkers to maintain constant count
            while (wanderingWalkers.size() < NUM_WALKERS) {
                Walker newWalker = new Walker(parent, cellSize, nrows, ncols);
                wanderingWalkers.add(newWalker);
            }
            
            toRemove.clear();
        }

        // Display grid (stopped walkers with colors based on aggregation order)
        for (int r = 0; r < nrows; r++) {
            for (int c = 0; c < ncols; c++) {
                if (grid[r][c] > 0) {
                    int order = grid[r][c];
                    
                    // Color gradient with cycle (repeats every 10 particles)
                    float hue = PApplet.map(order, 0, 800, 200, 0);
                    parent.colorMode(PApplet.HSB, 360, 100, 100);
                    parent.fill(hue, 80, 90);
                    parent.colorMode(PApplet.RGB, 255, 255, 255);
                    
                    parent.rect(c * cellSize, r * cellSize, cellSize, cellSize);
                }
            }
        }
        
        // Display wandering walkers
        for (Walker w : wanderingWalkers) {
            w.display(parent);
        }
        
    }

    @Override
    public void keyPressed(PApplet parent) {
    }

    @Override
    public void mousePressed(PApplet parent) {
    }
    
}