package it.unibo.ai.didattica.competition.tablut.predictor;

import java.util.List;

import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class CurrentGame implements Game<WrapperState, Action, State.Turn> {

	private CheckPossibleMoves rules;
	private WrapperState initialState;
	private int numberOfBranches;

	public CurrentGame(WrapperState initialState, CheckPossibleMoves rules) {
		this.initialState = initialState;
		this.rules = rules;
		numberOfBranches = 3;
	}

	@Override
	public WrapperState getInitialState() {
		return initialState;
	}

	@Override
	public Turn[] getPlayers() {
		Turn[] players = new State.Turn[2];
		players[0] = State.Turn.WHITE;
		players[1] = State.Turn.BLACK;
		return players;
	}

	@Override
	public Turn getPlayer(WrapperState state) {
		return state.getState().getTurn();
	}

	@Override
	public List<Action> getActions(WrapperState state) {
		return AllMoves.getAllmoves(state.getState());
	}

	@Override
	public WrapperState getResult(WrapperState state, Action action) {
		State risState = null;
		WrapperState wrpRis = null;
		int numberOfTurn = state.getTurn() + 1;
		try {
			risState = rules.makeMove(state.getState(), action);
		} catch (Exception e) {
			System.out.println("Oh oh something wrong in CurrentGame getResult");
			e.printStackTrace();
		}
		wrpRis = new WrapperState(risState, numberOfTurn);
		return wrpRis;
	}

	@Override
	public boolean isTerminal(WrapperState state) {
		if (state.getState().getTurn().equalsTurn(State.Turn.BLACKWIN.toString())
				|| state.getState().getTurn().equalsTurn(State.Turn.WHITEWIN.toString())
				|| state.getState().getTurn().equalsTurn(State.Turn.DRAW.toString())) {
			return true;
		} else if (state.getTurn() - initialState.getTurn() >= numberOfBranches) {
			return true;
		}
		return false;
	}

	@Override
	public double getUtility(WrapperState state, Turn player) {
		int ris = 16 - state.getState().getNumberOf(Pawn.BLACK);;
		if (player.equalsTurn(Turn.BLACK.toString())) {
			ris = 1; // State.getState().getNumberOf(Pawn.WHITE) + state.getState().getNumberOf(Pawn.KING);
		} else if (player.equalsTurn(Turn.WHITE.toString())) {
			ris = state.getState().getNumberOf(Pawn.BLACK); // state.getState().getNumberOf(Pawn.WHITE)
		}
		return ris;
	}

}
