import processing.core.PApplet;

public class JogoDaVidaProcessing implements IProcessingApp {
	/**
	 * Press SPACE BAR to pause and toggle cells with the mouse.
	 * Press 'R' to randomize the grid and 'C' to clear it.
	 */

	private static final int NUMBER_OF_STATES = 2;
	private static final int NEIGHBOR_RADIUS = 1;

	private int cellSize = 10;
	private float probabilityOfAliveAtStart = 15f;
	private int interval = 100;
	private int lastRecordedTime = 0;
	private boolean pause = false;
	private LifeAutomata automata;
	private int[] stateColors;

	@Override
	public void setup(PApplet parent) 
	{
		parent.noSmooth();
		parent.noStroke();

		int ncols = PApplet.max(1, parent.width / cellSize);
		int nrows = PApplet.max(1, parent.height / cellSize);

		automata = new LifeAutomata(parent, nrows, ncols, NEIGHBOR_RADIUS);
		stateColors = new int[] { parent.color(0), parent.color(0, 200, 0) };
		automata.setStateColors(stateColors);
		automata.randomize(probabilityOfAliveAtStart);

		lastRecordedTime = parent.millis();
	}

	@Override
	public void draw(PApplet parent, float dt) 
	{
		parent.background(0);
		automata.display();

		if (!pause && parent.millis() - lastRecordedTime > interval) 
		{
			automata.step();
			lastRecordedTime = parent.millis();
		}
	}

	@Override
	public void keyPressed(PApplet parent)
	{
		if (parent.key == ' ')
		{
			pause = !pause;
			return;
		}

		if (parent.key == 'r' || parent.key == 'R')
		{
			automata.randomize(probabilityOfAliveAtStart);
		}
		else if (parent.key == 'c' || parent.key == 'C')
		{
			automata.clear();
		}
	}

	@Override
	public void mousePressed(PApplet parent) 
	{
		if (!pause)
		{
			return;
		}

		LifeCell cell = (LifeCell) automata.getCell(parent.mouseX, parent.mouseY);
		cell.flipState();
	}

	private static class LifeAutomata extends CellularAutomata 
	{
		public LifeAutomata(PApplet p, int nrows, int ncols, int radius)
		{
			super(p, nrows, ncols, radius, NUMBER_OF_STATES);
		}

		@Override
		protected void createGrid()
		{
			cells = new Cell[nrows][ncols];
			for (int i = 0; i < nrows; i++) {
				for (int j = 0; j < ncols; j++) {
					cells[i][j] = new LifeCell(this, i, j);
				}
			}
			setNeighbors();
		}

		public void randomize(float aliveProbabilityPercent)
		{
			for (int i = 0; i < nrows; i++) {
				for (int j = 0; j < ncols; j++) {
					LifeCell cell = (LifeCell) cells[i][j];
					float roll = p.random(100f);
					cell.setState(roll < aliveProbabilityPercent ? 1 : 0);
				}
			}
		}

		public void clear()
		{
			for (int i = 0; i < nrows; i++) {
				for (int j = 0; j < ncols; j++) {
					cells[i][j].setState(0);
				}
			}
		}

		public void step()
		{
			for (int i = 0; i < nrows; i++) {
				for (int j = 0; j < ncols; j++) {
					((LifeCell) cells[i][j]).countAlives();
				}
			}

			for (int i = 0; i < nrows; i++) {
				for (int j = 0; j < ncols; j++) {
					((LifeCell) cells[i][j]).planNextState();
				}
			}

			for (int i = 0; i < nrows; i++) {
				for (int j = 0; j < ncols; j++) {
					((LifeCell) cells[i][j]).applyNextState();
				}
			}
		}
	}
}
