package it.unibo.ai.didattica.competition.tablut.client;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface ClientController {
	
	public boolean canMove(State state, int row, int column);
	
	public String chooseDestination(State state, int row, int column);

}
