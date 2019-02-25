package it.unibo.ai.didattica.competition.tablut.domain;


/**
 * Abstract class for a State of a game
 * We have a representation of the board and the turn 
 * @author Andrea Piretti
 *
 */
public abstract class State {
	
	/**
	 * Turn represent the player that has to move or the end of the game(A win by a player or a draw)
	 * @author A.Piretti
	 */
	public enum Turn 
	{
		WHITE("W"), BLACK("B"), WHITEWIN("WW"), BLACKWIN("BW"), DRAW("D");
		private final String turn;

		private Turn(String s) {
			turn = s;
		}

		public boolean equalsTurn(String otherName) {
			return (otherName == null) ? false : turn.equals(otherName);
		}

		public String toString() {
			return turn;
		}
	}

	/**
	 * 
	 * Pawn represents the content of a box in the board
	 * @author A.Piretti
	 *
	 */
	public enum Pawn
	{
		EMPTY("O"), WHITE("W"), BLACK("B"), THRONE("T"), KING("K");
		private final String pawn;

		private Pawn(String s) {
			pawn = s;
		}

		public boolean equalsPawn(String otherPawn) {
			return (otherPawn == null) ? false : pawn.equals(otherPawn);
		}

		public String toString() {
			return pawn;
		}

	}
	
	protected Pawn board[][];
	protected Turn turn;
	
	public State()
	{
		super();
	}

	public Pawn[][] getBoard() {
		return board;
	}

	public String boardString()
	{
		StringBuffer result = new StringBuffer();
		for(int i=0; i<this.board.length; i++)
		{
			for(int j=0; j<this.board.length; j++)
			{
				result.append(this.board[i][j].toString());
				if(j==8)
				{
					result.append("\n");
				}
			}
		}
		return result.toString();
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		
		//board
		result.append("");
		result.append(this.boardString());
		
		result.append("-");
		result.append("\n");
		
		//TURNO
		result.append(this.turn.toString());
		
		return result.toString();
	}
	
	public StateTablut clone() {
		StateTablut result = new StateTablut();
		result.setBoard(this.board);
		result.setTurn(this.turn);
		return result;
	}

	/**
	 * this function tells the pawn inside a specific box on the board
	 * @param row
	 * 		represents the row of the specific box
	 * @param column
	 * 		represents the column of the specific box
	 * @return is the pawn of the box
	 */
	public Pawn getPawn(int row, int column)
	{
		return this.board[row][column];
	}
	
	/**
	 * this function remove a specified pawn from the board
	 * @param row
	 * 		represents the row of the specific box
	 * @param column
	 * 		represents the column of the specific box
	 * 
	 */
	public void removePawn(int row, int column)
	{
		this.board[row][column] = Pawn.EMPTY;
	}
	
	public void setBoard(Pawn[][] board) {
		this.board = board;
	}

	public Turn getTurn() {
		return turn;
	}

	public void setTurn(Turn turn) {
		this.turn = turn;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StateTablut other = (StateTablut) obj;
		if (this.board == null) {
			if (other.board != null)
				return false;
		} else if (!this.board.equals(other.board))
			return false;
		if (this.turn != other.turn)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.board == null) ? 0 : this.board.hashCode());
		result = prime * result + ((this.turn == null) ? 0 : this.turn.hashCode());
		return result;
	}

	public String getBox(int row, int column)
	{
		String ret;
		char col = (char) (column+97);
		ret=col+""+(row+1);
		return ret;
	}
	
	
}
