package it.unibo.ai.didattica.competition.tablut.gui.client;

/**
 * Auxiliary class that represents a position on the board (row x column).
 * 
 * @author Michele Righi
 * (<a href="https://github.com/mikyll">GitHub</a>,
 * <a href="https://www.linkedin.com/in/michele-righi/">LinkedIn</a>)
 */
public class Coordinate {
	private int row;
	private int col;
	
	public Coordinate(int row, int col) {
		this.row = row;
		this.col = col;
	}
	public Coordinate() {
		reset();
	}
	
	public void reset() {
		row = -1;
		col = -1;
	}
	
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public int getCol() {
		return col;
	}
	public void setCol(int col) {
		this.col = col;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
            return true;
		
		if (!(o instanceof Coordinate))
			return false;
		
		Coordinate c = (Coordinate) o;
		
		return this.row == c.getRow() && this.col == c.getCol();
	}
	
	public String toString() {
		return "(" + row + "," + col + ")";
	}
	public String toGameString() {
		char chCol = (char) (col+97);
		return "" + chCol + (row+1);
	}
}
