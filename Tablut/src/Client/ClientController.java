package Client;

import Domain.State;

public interface ClientController {
	
	public boolean canMove(State state, int row, int column);
	
	public String chooseDestination(State state, int row, int column);

}
