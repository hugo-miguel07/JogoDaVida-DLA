import processing.core.PApplet;

public class ProcessingSetup extends PApplet {
    private static IProcessingApp app;
    private float lastUpdateTime;

    public void settings(){
        size(1280, 720);
    }

    public void setup(){
        app.setup(this);
        lastUpdateTime = millis();
    }
    
    public void draw(){
        float now = millis();
        float dt = (now - lastUpdateTime) / 1000f;
        lastUpdateTime = now;
        app.draw(this, dt);

    }

    public void mousePressed() {
        app.mousePressed(this);
    }

    public void keyPressed() {
        app.keyPressed(this);
    }
    public static void main(String[] args) {
        app = new DLA();
        //app = new JogoDaVidaProcessing();
        PApplet.main(ProcessingSetup.class.getName());
    }
}
