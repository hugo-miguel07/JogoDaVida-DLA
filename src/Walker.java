import processing.core.PApplet;

public class Walker {
    
    public enum State {
        STOPPED,
        WANDER
    }

    private int row, col;
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
        
        this.row = nrows / 2;
        this.col = ncols / 2;
        
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
            // Cena da Cor
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
        int centerRow = nrows / 2;
        int centerCol = ncols / 2;
        
        float biasStrength = 0.015f;
        
        if (p.random(1) < biasStrength) {
            int dRow = centerRow - row;
            int dCol = centerCol - col;
            
            //Check para saber como anda na coluna ou na linha
            if (Math.abs(dRow) > Math.abs(dCol)) {
                if (dRow > 0) row++;
                else if (dRow < 0) row--;
                else {
                    if (dCol > 0) col++;
                    else if (dCol < 0) col--;
                }
            } else {
                if (dCol > 0) col++;
                else if (dCol < 0) col--;
                else {
                    if (dRow > 0) row++;
                    else if (dRow < 0) row--;
                }
            }
        } else {
            int dir = (int)p.random(4);
            
            if (dir == 0) row--;
            else if (dir == 1) row++;
            else if (dir == 2) col--;
            else col++;
        }
        
        if (row < 0) row = nrows - 1;
        if (row >= nrows) row = 0;
        if (col < 0) col = ncols - 1;
        if (col >= ncols) col = 0;
    }

    public void updateState(PApplet p, int[][] grid)
    {
        //Von Neuman
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == dc || -dr == dc) continue;
                
                int checkRow = (row + dr + nrows) % nrows;
                int checkCol = (col + dc + ncols) % ncols;
                
                if (grid[checkRow][checkCol] > 0) {
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