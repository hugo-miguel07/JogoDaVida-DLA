import processing.core.PApplet;

public class Walker {
    
    public enum State {
        STOPPED,
        WANDER
    }

    private int row, col;  // Grid position instead of continuous position
    private State state;
    private int colour;
    private int cellSize;
    private int aggregationOrder;
    private int nrows, ncols;

    public Walker(PApplet p, int cellSize, int nrows, int ncols)
    {
        this.cellSize = cellSize;
        this.nrows = nrows;
        this.ncols = ncols;
        
        // Start at center
        this.row = nrows / 2;
        this.col = ncols / 2;
        
        // Random position on edge
        int edge = (int)p.random(4);
        if (edge == 0) { // Top
            row = 0;
            col = (int)p.random(ncols);
        } else if (edge == 1) { // Right
            row = (int)p.random(nrows);
            col = ncols - 1;
        } else if (edge == 2) { // Bottom
            row = nrows - 1;
            col = (int)p.random(ncols);
        } else { // Left
            row = (int)p.random(nrows);
            col = 0;
        }

        setState(p, State.WANDER);
        aggregationOrder = 0;
    }

    public Walker(PApplet p, int cellSize, int nrows, int ncols, int row, int col) {
        this.cellSize = cellSize;
        this.nrows = nrows;
        this.ncols = ncols;
        this.row = row;
        this.col = col;

        setState(p, State.STOPPED);
        aggregationOrder = 0;
    }

    public int getAggregationOrder() {
        return aggregationOrder;
    }

    public void setAggregationOrder(int order) {
        this.aggregationOrder = order;
    }

    public void setState(PApplet p, State state)
    {
        this.state = state;
        if (state == State.STOPPED)
        {
            // Color based on aggregation order - faster transition (reaches red at 50 particles)
            float hue = PApplet.map(aggregationOrder, 0, 10, 200, 0);
            p.colorMode(PApplet.HSB, 360, 100, 100);
            colour = p.color(hue, 80, 90);
            p.colorMode(PApplet.RGB, 255, 255, 255);
        }
        else
        {
            colour = p.color(255);
        }

    }

    public int getRow() {
        return row;
    }
    
    public int getCol() {
        return col;
    }

    public State getState()
    {
        return state;
    }

    public void wander(PApplet p)
    {
        // Calculate direction toward center
        int centerRow = nrows / 2;
        int centerCol = ncols / 2;
        
        float biasStrength = 0.015f;
        
        if (p.random(1) < biasStrength) {
            // Move toward center
            int dRow = centerRow - row;
            int dCol = centerCol - col;
            
            // Choose whether to move in row or col direction
            if (Math.abs(dRow) > Math.abs(dCol)) {
                // Move vertically toward center
                if (dRow > 0) row++; // Move down
                else if (dRow < 0) row--; // Move up
                else {
                    // Already aligned vertically, move horizontally
                    if (dCol > 0) col++;
                    else if (dCol < 0) col--;
                }
            } else {
                // Move horizontally toward center
                if (dCol > 0) col++; // Move right
                else if (dCol < 0) col--; // Move left
                else {
                    // Already aligned horizontally, move vertically
                    if (dRow > 0) row++;
                    else if (dRow < 0) row--;
                }
            }
        } else {
            // Random walk in 4 directions
            int dir = (int)p.random(4);
            
            if (dir == 0) row--; // Up
            else if (dir == 1) row++; // Down
            else if (dir == 2) col--; // Left
            else col++; // Right
        }
        
        // Wrap around edges
        if (row < 0) row = nrows - 1;
        if (row >= nrows) row = 0;
        if (col < 0) col = ncols - 1;
        if (col >= ncols) col = 0;
    }

    public void updateState(PApplet p, int[][] grid)
    {
        // Check Moore neighborhood (8 neighbors)
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == dc || -dr == dc) continue; // Skip self
                
                int checkRow = (row + dr + nrows) % nrows;
                int checkCol = (col + dc + ncols) % ncols;
                
                if (grid[checkRow][checkCol] > 0) { // Cell is occupied
                    setState(p, State.STOPPED);
                    return;
                }
            }
        }
    }

    public void display(PApplet p)
    {
        p.fill(colour);
        p.rect(col * cellSize, row * cellSize, cellSize, cellSize);
    }
}