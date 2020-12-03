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
		numberOfBranches = 4;
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
		double ris = 16 - state.getState().getNumberOf(Pawn.BLACK);
		int x = 0;
		int y = 0;
		// Black
		if (player.equalsTurn(Turn.BLACK.toString())) {
			int whiteBlocked = 0;
			int L = state.getState().getBoard().length;
			int countTot = 0;
			double weigthKing = 5;

//			Pawn[][] board = state.getState().getBoard();
			for (int i = 0; i < state.getState().getBoard().length; ++i) {
				for (int j = 0; j < state.getState().getBoard().length; ++j) {
//					if (board[i][j].equals(Pawn.BLACK)) {
//						if ((i + 1 < L && board[i + 1][j].equals(Pawn.WHITE))) {
//							++whiteBlocked;
//						}
//						if ((i - 1 >= 0 && board[i - 1][j].equals(Pawn.WHITE))) {
//							++whiteBlocked;
//						}
//						if ((j - 1 >= 0 && board[i][j - 1].equals(Pawn.WHITE))) {
//							++whiteBlocked;
//						}
//						if ((j + 1 < L && board[i][j + 1].equals(Pawn.WHITE))) {
//							++whiteBlocked;
//						}
//					}
					if (state.getState().getPawn(i, j).equals(Pawn.WHITE)
							|| state.getState().getPawn(i, j).equals(Pawn.KING)) {
						int count = 0;

						for (int k = i - 1; k >= 0; k--) {
							Pawn pawnBlack = state.getState().getPawn(k, j);
							if (pawnBlack.equals(Pawn.BLACK))
								break;
							count++;
						}

						for (int k = i + 1; k < state.getState().getBoard().length; k++) {
							Pawn pawnBlack = state.getState().getPawn(k, j);
							if (pawnBlack.equals(Pawn.BLACK))
								break;
							count++;
						}

						for (int k = j - 1; k >= 0; k--) {
							Pawn pawnBlack = state.getState().getPawn(k, j);
							if (pawnBlack.equals(Pawn.BLACK))
								break;
							count++;
						}

						for (int k = j + 1; k < state.getState().getBoard().length; k++) {
							Pawn pawnBlack = state.getState().getPawn(k, j);
							if (pawnBlack.equals(Pawn.BLACK))
								break;
							count++;
						}

						if (state.getState().getPawn(i, j).equals(Pawn.KING))
							count *= weigthKing;

						countTot += count;
					}
				}
			}

			if (state.getState().getTurn().equalsTurn(State.Turn.BLACKWIN.toString())) {
				return ris = 99999999 / (state.getTurn() - initialState.getTurn() + 1);
			}

			if (state.getState().getTurn().equalsTurn(State.Turn.WHITEWIN.toString())) {
				return ris = -99999999;
			}

//			double weight = initialState.getTurn() / 25;
			double weigthBlack = 0.7;
			double weigthWhite = 1;
			double weigthBlocked = 1 / 176.0;

			if (state.getTurn() >= 4)
				weigthBlocked *= 6;

			double black = state.getState().getNumberOf(Pawn.BLACK) * weigthBlack;
			double white = state.getState().getNumberOf(Pawn.WHITE) * weigthWhite;
			double blocked = countTot * weigthBlocked;

			ris = black - blocked - white;// +whiteBlocked * weight
		}
		// White
		else if (player.equalsTurn(Turn.WHITE.toString())) {
			int L = state.getState().getBoard().length;
			Pawn[][] board = state.getState().getBoard();
			for (int i = 0; i < state.getState().getBoard().length; ++i) {
				for (int j = 0; j < state.getState().getBoard().length; ++j) {
					if (board[i][j].equals(Pawn.KING)) {
						x = i;
						y = j;
						break;
					}
				}
			}
			// Corner top-left
			double dist01 = Math.sqrt(Math.pow((x - 0), 2) + Math.pow((y - 1), 2));
			double dist10 = Math.sqrt(Math.pow((x - 1), 2) + Math.pow((y - 0), 2));
			double dist02 = Math.sqrt(Math.pow((x - 0), 2) + Math.pow((y - 2), 2));
			double dist20 = Math.sqrt(Math.pow((x - 2), 2) + Math.pow((y - 0), 2));

			double mintl1 = Math.min(dist01, dist10);
			double mintl2 = Math.min(dist02, dist20);

			double mintl = Math.min(mintl1, mintl2);

			// Corner top-right
			double distL1 = Math.sqrt(Math.pow((x - L), 2) + Math.pow((y - 1), 2));
			double distL2 = Math.sqrt(Math.pow((x - L), 2) + Math.pow((y - 2), 2));
			double distL10 = Math.sqrt(Math.pow((x - L - 1), 2) + Math.pow((y - 0), 2));
			double distL20 = Math.sqrt(Math.pow((x - L - 2), 2) + Math.pow((y - 0), 2));

			double mintr1 = Math.min(distL1, distL2);
			double mintr2 = Math.min(distL10, distL20);

			double minrl = Math.min(mintr1, mintr2);

			// Corner bottom-left
			double dist0L1 = Math.sqrt(Math.pow((x - 0), 2) + Math.pow((y - L - 1), 2));
			double dist0L2 = Math.sqrt(Math.pow((x - 0), 2) + Math.pow((y - L - 2), 2));
			double dist01L1 = Math.sqrt(Math.pow((x - 1), 2) + Math.pow((y - L), 2));
			double dist1L2 = Math.sqrt(Math.pow((x - 2), 2) + Math.pow((y - L), 2));

			double minbl1 = Math.min(dist0L1, dist0L2);
			double minbl2 = Math.min(dist01L1, dist1L2);

			double minbl = Math.min(minbl1, minbl2);

			// Corner bottom-right
			double distLL1 = Math.sqrt(Math.pow((x - L), 2) + Math.pow((y - L - 1), 2));
			double distLL2 = Math.sqrt(Math.pow((x - L), 2) + Math.pow((y - L - 2), 2));
			double distL2L = Math.sqrt(Math.pow((x - L - 2), 2) + Math.pow((y - L), 2));
			double distL1L = Math.sqrt(Math.pow((x - L - 1), 2) + Math.pow((y - L), 2));

			double minbr1 = Math.min(distLL1, distLL2);
			double minbr2 = Math.min(distL2L, distL1L);

			double minbr = Math.min(minbr1, minbr2);

			double min1 = Math.min(mintl, minrl);
			double min2 = Math.min(minbl, minbr);
			double dist = Math.min(min1, min2);

			if (state.getState().getTurn().equalsTurn(State.Turn.BLACKWIN.toString())) {
				return ris = -99999999;
			}

			if (state.getState().getTurn().equalsTurn(State.Turn.WHITEWIN.toString())) {
				return ris = 99999999 / (state.getTurn() - initialState.getTurn() + 1);
			}

			double weigthWhite = 1.2;

			int numbBlack = state.getState().getNumberOf(Pawn.BLACK);

			double weigthDist = (17 - numbBlack) * 0.07;

			if (state.getTurn() >= 4)
				weigthWhite = 0.7;

			ris = state.getState().getNumberOf(Pawn.WHITE) * weigthWhite - dist * weigthDist - numbBlack; //
			// state.getState().getNumberOf(Pawn.WHITE)
		}
		return ris;
	}

}
