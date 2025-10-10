public class LifeCell extends Cell 
{
	private int nAlives;
	private int nextState;

	public LifeCell(CellularAutomata ca, int row, int col)
	{
		super(ca, row, col);
		nextState = state;
	}
	
	@Override
	public void setState(int state)
	{
		super.setState(state);
		nextState = state;
	}
	
	public void flipState()
	{
		if (state == 0) {
			setState(1);
		}
		else {
			setState(0);
		}
	}
	
	public void countAlives()
	{
		nAlives = 0;
		for (Cell c : neighbors) nAlives += c.state;
		nAlives -= state;
	}

	public void planNextState()
	{
		nextState = state;
		if (state == 0 && nAlives == 3) nextState = 1;
		if (state == 1 && (nAlives < 2 || nAlives > 3)) nextState = 0;
	}

	public void applyNextState()
	{
		state = nextState;
	}
}
