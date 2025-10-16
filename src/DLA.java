import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;

public class DLA implements IProcessingApp 
{
    private int NUM_WALKERS = 500;
    private int NUM_STEPS_PER_FRAME = 100;
    private List<Walker> walkers;
    private int aggregationCounter;
    
    @Override
    public void setup(PApplet parent) 
    {
        walkers = new ArrayList<Walker>();
        aggregationCounter = 0;
        
        // Create the seed particle at the center
        Walker w = new Walker(parent, new PVector(parent.width / 2, parent.height / 2));
        w.setAggregationOrder(aggregationCounter++);
        walkers.add(w);
        
        // Create initial wandering particles
        for (int i=0; i<NUM_WALKERS;i++)
        {
            w = new Walker(parent);
            walkers.add(w);
        }
    }

    @Override
    public void draw(PApplet parent, float dt) 
    {
        parent.background(200);

        for (int i = 0; i < NUM_STEPS_PER_FRAME; i++)
        {
            for (int j = 0; j < walkers.size(); j++) {
                Walker w = walkers.get(j);
                if (w.getState() == Walker.State.WANDER)
                {
                    w.wander(parent);
                    Walker.State previousState = w.getState();
                    w.updateState(parent, walkers);
                    
                    // If walker just stopped, create a new one to maintain constant count
                    if (previousState == Walker.State.WANDER && w.getState() == Walker.State.STOPPED) {
                        w.setAggregationOrder(aggregationCounter++);
                        w.setState(parent, Walker.State.STOPPED); // Update color based on order
                        
                        // Spawn a new wandering walker
                        Walker newWalker = new Walker(parent);
                        walkers.add(newWalker);
                    }
                }  
            }
        }

        for (Walker w : walkers) {
            w.display(parent);
        }
        
    }

    @Override
    public void keyPressed(PApplet parent) {
        // Empty implementation - no special key handling needed
    }

    @Override
    public void mousePressed(PApplet parent) {
        // Empty implementation - no mouse interaction needed
    }
    
}