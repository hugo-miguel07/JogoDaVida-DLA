import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public class DLA implements IProcessingApp 
{
    private int NUM_WALKERS = 200;
    private int NUM_STEPS_PER_FRAME = 20;
    private int cellSize = 5;
    private int nrows, ncols;
    private boolean start = false;
    
    private List<Walker> wanderingWalkers;
    private int[][] grid;
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
        /* int centerRow = nrows / 2;
        int centerCol = ncols / 2;
        aggregationCounter++;
        grid[centerRow][centerCol] = aggregationCounter; */

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

        if (!this.start)
        {
            //Escrever as coisas la em cima
		    parent.fill(40, 40, 40);
		    parent.textSize(24);
				
		    String statusText = this.start ? "A CORRER" : "ESCOLHA";
		
            parent.text(statusText, parent.width/2 - 70, 30);
            
            parent.textSize(16);
            parent.fill(20);
            parent.text("SPACE=1 cell centro | C=Circulo |"
                + " Q=Quadrado | L=linha", parent.width/2 - 240, 50);
            return ;
        }

        List<Walker> toRemove = new ArrayList<Walker>();
        
        for (int i = 0; i < NUM_STEPS_PER_FRAME; i++)
        {
            for (Walker w : wanderingWalkers) {
                w.wander(parent);
                w.updateState(parent, grid);
                
                if (w.getState() == Walker.State.STOPPED) {
                    aggregationCounter++;
                    
                    grid[w.getRow()][w.getCol()] = aggregationCounter;
                    toRemove.add(w);
                }
            }
            
            wanderingWalkers.removeAll(toRemove);
            
            while (wanderingWalkers.size() < NUM_WALKERS) {
                Walker newWalker = new Walker(parent, cellSize, nrows, ncols);
                wanderingWalkers.add(newWalker);
            }
            
            toRemove.clear();
        }

        for (int r = 0; r < nrows; r++) {
            for (int c = 0; c < ncols; c++) {
                if (grid[r][c] > 0) {
                    int order = grid[r][c];
                    
                    //Codigo do Gradiante de Cor
                    float hue = PApplet.map(order, 0, 1000, 200, 0);
                    parent.colorMode(PApplet.HSB, 360, 100, 100);
                    parent.fill(hue, 80, 90);
                    parent.colorMode(PApplet.RGB, 255, 255, 255);
                    
                    parent.rect(c * cellSize, r * cellSize, cellSize, cellSize);
                }
            }
        }
        
        for (Walker w : wanderingWalkers) {
            w.display(parent);
        }
        
    }

    @Override
    public void keyPressed(PApplet parent) {
        if (!start)
        {
            if (parent.key == ' ')
            {
               this.start = !start;
                createCenterParticle(); 
            }
            else if (parent.key == 'C' || parent.key == 'c')
            {
                this.start = !start;
                createCircle();
            }
            else if (parent.key == 'Q' || parent.key == 'q')
            {
                this.start = !start;
                createSquare();
            }
            else if (parent.key == 'L' || parent.key == 'l')
            {
                this.start = !start;
                createLine();
            }
        }
    }

    @Override
    public void mousePressed(PApplet parent) {
    }
    
    void createCenterParticle()
    {
       int centerRow = nrows / 2;
        int centerCol = ncols / 2;
        this.aggregationCounter++;
        this.grid[centerRow][centerCol] = this.aggregationCounter; 
    }

    void createSquare()
    {
        int centerRow = nrows / 2;
        int centerCol = ncols / 2;
        this.aggregationCounter++;
        for (int i = 0; i < 10; i++)
        {
            this.grid[centerRow - 5 + i][centerCol - 5] = this.aggregationCounter;
            this.grid[centerRow - 5 + i][centerCol + 5] = this.aggregationCounter;
        }
        for (int i = 0; i < 10; i++)
        {
            this.grid[centerRow - 5][centerCol - 5 + i] = this.aggregationCounter;
            this.grid[centerRow + 5][centerCol - 5 + i] = this.aggregationCounter;
        }
        this.grid[centerRow + 5][centerCol + 5] = this.aggregationCounter; 
    }

    void createLine()
    {
        int centerRow = nrows / 2;
        int centerCol = ncols / 2;
        this.aggregationCounter++;
        for (int i = 0; i <= 16; i++)
            this.grid[centerRow][centerCol + 8 - i] = this.aggregationCounter;
    }

    void createCircle()
    {
        int centerRow = nrows / 2;
        int centerCol = ncols / 2;
        int raio = 7;
        
        this.aggregationCounter++;
        
        for (int row = centerRow - raio; row <= centerRow + raio; row++)
        {
            for (int col = centerCol - raio; col <= centerCol + raio; col++)
            {
                //Distacia do centro
                int dr = row - centerRow;
                int dc = col - centerCol;
                double distance = Math.sqrt(dr * dr + dc * dc);
                
                //if distancia for aproximadamente igual ao raio, pinta a particula
                if (distance >= raio - 0.5 && distance <= raio + 0.5)
                {
                    if (row >= 0 && row < nrows && col >= 0 && col < ncols)
                    {
                        this.grid[row][col] = this.aggregationCounter;
                    }
                }
            }
        }
    }
}