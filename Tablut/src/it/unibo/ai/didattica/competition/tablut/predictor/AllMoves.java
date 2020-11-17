package it.unibo.ai.didattica.competition.tablut.predictor;

import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public class AllMoves {

	private static CheckPossibleMoves rules = new CheckPossibleMoves(99, 0, "garbage", "fake", "fake");

	public static List<Action> getAllmoves(State stato) {
		List<Action> ris = new ArrayList<Action>();
		Action tmp = null;

		State.Turn turn = null;
		State.Pawn pawn = null;
		State.Pawn pawn2 = null;

		if (stato.getTurn().compareTo(State.Turn.BLACK) == 0) {
			turn = State.Turn.BLACK;
			pawn = State.Pawn.BLACK;
			pawn2 = State.Pawn.BLACK;
		} else {
			turn = State.Turn.WHITE;
			pawn = State.Pawn.WHITE;
			pawn2 = State.Pawn.KING;
		}

		for (int i = 0; i < stato.getBoard().length; i++) {
			for (int j = 0; j < stato.getBoard().length; j++) {
				// If the pawn is black the second condition is not even evaluated,
				// evaluated also if is turn black but white pawn
				if (stato.getPawn(i, j).equalsPawn(pawn.toString())
						|| stato.getPawn(i, j).equalsPawn(pawn2.toString())) {

					boolean stop = false;
					String from = stato.getBox(i, j);

					// 4 for for each direction (up, down, left, right)
					for (int k = i - 1; k >= 0 && !stop; k--) {
						String to = stato.getBox(k, j);
						try {
							tmp = new Action(from, to, turn);
							rules.checkMove(stato, tmp);
						} catch (Exception e1) {
							stop = true;
						}
						if (!stop) {
							ris.add(tmp);
						}
					}
					stop = false;

					for (int k = i + 1; k < stato.getBoard().length; k++) {
						String to = stato.getBox(k, j);
						try {
							tmp = new Action(from, to, turn);
							rules.checkMove(stato, tmp);
						} catch (Exception e1) {
							stop = true;
						}
						if (!stop) {
							ris.add(tmp);
						}
					}
					stop = false;

					for (int k = j - 1; k >= 0 && !stop; k--) {
						String to = stato.getBox(i, k);
						try {
							tmp = new Action(from, to, turn);
							rules.checkMove(stato, tmp);
						} catch (Exception e1) {
							stop = true;
						}
						if (!stop) {
							ris.add(tmp);
						}
					}
					stop = false;

					for (int k = j + 1; k < stato.getBoard().length && !stop; k++) {
						String to = stato.getBox(i, k);
						try {
							tmp = new Action(from, to, turn);
							rules.checkMove(stato, tmp);
						} catch (Exception e1) {
							stop = true;
						}
						if (!stop) {
							ris.add(tmp);
						}
					}
				}
			}
		}

		return ris;
	}

}
