package it.unibo.ai.didattica.competition.tablut.predictor;

import java.util.List;

import aima.core.search.adversarial.Game;
import it.unibo.ai.didattica.competition.tablut.client.PlayerAI;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.exceptions.ActionException;
import it.unibo.ai.didattica.competition.tablut.exceptions.BoardException;
import it.unibo.ai.didattica.competition.tablut.exceptions.CitadelException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingCitadelException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingException;
import it.unibo.ai.didattica.competition.tablut.exceptions.DiagonalException;
import it.unibo.ai.didattica.competition.tablut.exceptions.OccupitedException;
import it.unibo.ai.didattica.competition.tablut.exceptions.PawnException;
import it.unibo.ai.didattica.competition.tablut.exceptions.StopException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ThroneException;


public class CurrentGame implements Game<State, Action , State.Turn> {

	
	public it.unibo.ai.didattica.competition.tablut.domain.Game rules;
	public State initialState;
	
	public CurrentGame(State initialState,it.unibo.ai.didattica.competition.tablut.domain.Game rules) {
		this.initialState=initialState;
		this.rules=rules;
	}
	
	
	@Override
	public State getInitialState() {
		return initialState;
	}

	@Override
	public Turn[] getPlayers() {
		Turn[] players = new State.Turn[2];
		players[0]=State.Turn.WHITE;
		players[1]=State.Turn.BLACK;
		return players;
	}

	@Override
	public Turn getPlayer(State state) {
		return state.getTurn();
	}

	@Override
	public List<Action> getActions(State state) {
		return AllMoves.getAllmoves(state);
	}

	@Override
	public State getResult(State state, Action action) {
		State ris=null;
		try {
			ris = rules.checkMove(state, action);
		} catch (Exception e) {
			System.out.println("Oh oh something wrong in CurrentGame getResult");
		}
		return ris;	
	}

	@Override
	public boolean isTerminal(State state) {
		if (state.getTurn().equalsTurn(State.Turn.BLACKWIN.toString()) 
			|| state.getTurn().equalsTurn(State.Turn.WHITEWIN.toString())
			|| state.getTurn().equalsTurn(State.Turn.DRAW.toString())) {
			return true;
		}
		return false;
	}

	@Override
	public double getUtility(State state, Turn player) {
		//inserire euristica
		return 0;
	}

	
	
	
	
	

}
