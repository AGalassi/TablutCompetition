package it.unibo.ai.didattica.competition.tablut.predictor;

import aima.core.search.adversarial.*;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class Predictor {

	private CurrentGame currentGame;
	private AlphaBetaSearch<WrapperState, Action, State.Turn> predictor;

	public Predictor(WrapperState currentState, CheckPossibleMoves rules) {
		currentGame = new CurrentGame(currentState, rules);
		predictor = AlphaBetaSearch.createFor(currentGame);
	}

	public Action findBestAction(WrapperState currentState) {
		return predictor.makeDecision(currentState);
	}

	public void setCurrentGame(CurrentGame currentGame) {
		this.currentGame = currentGame;
	}

}
