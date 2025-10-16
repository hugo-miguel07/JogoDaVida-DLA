import java.util.List;

import processing.core.PApplet;
import processing.core.PVector;

public class Walker {
    
    public enum State {
        STOPPED,
        WANDER
    }

    private PVector pos;
    private State state;
    private int colour;
    private static int radius = 1;
    private int aggregationOrder;

    public Walker(PApplet p)
    {
        //pos = new PVector(p.random(p.width), p.random(p.height));
        pos = new PVector(p.width / 2, p.height / 2);
        PVector d = PVector.random2D();
        pos.add(d.mult(p.height / 2));

        setState(p, State.WANDER);
        aggregationOrder = 0;
    }

    public Walker(PApplet p, PVector pos) {
        this.pos = pos;

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
            // Color based on aggregation order - gradient from blue to red
            float hue = PApplet.map(aggregationOrder, 0, 500, 200, 0);
            p.colorMode(PApplet.HSB, 360, 100, 100);
            colour = p.color(hue, 80, 90);
            p.colorMode(PApplet.RGB, 255, 255, 255);
        }
        else
        {
            colour = p.color(255);
        }

    }

    public PVector getPos() {
        return pos;
    }

    public State getState()
    {
        return state;
    }

    public void wander(PApplet p)
    {
        PVector step = PVector.random2D();
        pos.add(step);

        pos.lerp(new PVector(p.width / 2, p.height / 2), 0.0002f);
    }

    public void updateState(PApplet p, List<Walker> walkers)
    {
        /*if (state == State.STOPPED)
            return;*/
        
        for (Walker w : walkers)
        {
            if (w.state == State.STOPPED) {
                float dist = PVector.dist(this.pos, w.pos);
                if (dist < 2 * radius) {
                    setState(p, State.STOPPED);
                    break;
                }
            }
        }
    }

    public void display(PApplet p)
    {
        p.fill(colour);
        p.circle(pos.x, pos.y, 2*radius);
    }
}