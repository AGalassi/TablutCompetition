package it.unibo.ai.didattica.competition.tablut.predictor;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class WrapperState {

	private State state;
	private int turn;

	public WrapperState(State state,int turn) {
		this.state=state;
		this.turn=turn;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}




}
