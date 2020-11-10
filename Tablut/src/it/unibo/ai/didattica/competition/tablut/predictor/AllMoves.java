package it.unibo.ai.didattica.competition.tablut.predictor;

import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;


public class AllMoves {
	public static Game rules =  new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
	
	
	public static List<Action> getAllmoves(State stato){
		List<Action> ris = new ArrayList<Action>();
		Action tmp=null;
		if (stato.getTurn().compareTo(State.Turn.BLACK)==0) {
			for (int i=0;i<stato.getBoard().length;i++) {
				for (int j=0;j<stato.getBoard().length;j++) {
					if (stato.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
						boolean stop=false;
						String from = stato.getBox(i, j);
						for (int k=0;k<i && !stop;k++) {
							String to = stato.getBox(k, j);
							try {
								tmp = new Action(from, to, State.Turn.BLACK);
								rules.checkMove(stato, tmp);
							} catch (Exception e1) {
								stop=true;
							}
							if (!stop) {
								ris.add(tmp);
							}
						}
						stop=false;
						for (int k=i+1;k<stato.getBoard().length;k++) {
							String to = stato.getBox(k, j);
							try {
								tmp = new Action(from, to, State.Turn.BLACK);
								rules.checkMove(stato, tmp);
							} catch (Exception e1) {
								stop=true;
							}
							if (!stop) {
								ris.add(tmp);
							}
						}
						stop=false;
						for (int k=0;k<j && !stop;k++) {
							String to = stato.getBox(i, k);
							try {
								tmp = new Action(from, to, State.Turn.BLACK);
								rules.checkMove(stato, tmp);
							} catch (Exception e1) {
								stop=true;
							}
							if (!stop) {
								ris.add(tmp);
							}
						}
						stop=false;
						for (int k=j+1;k<stato.getBoard().length && !stop;k++) {
							String to = stato.getBox(i, k);
							try {
								tmp = new Action(from, to, State.Turn.BLACK);
								rules.checkMove(stato, tmp);
							} catch (Exception e1) {
								stop=true;
							}
							if (!stop) {
								ris.add(tmp);
							}
						}				
					}
				}
			}
	} else if (stato.getTurn().compareTo(State.Turn.WHITE)==0) {
		for (int i=0;i<stato.getBoard().length;i++) {
			for (int j=0;j<stato.getBoard().length;j++) {
				if (stato.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString()) || stato.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
					boolean stop=false;
					String from = stato.getBox(i, j);
					for (int k=0;k<i && !stop;k++) {
						String to = stato.getBox(k, j);
						try {
							tmp = new Action(from, to, State.Turn.WHITE);
							rules.checkMove(stato, tmp);
						} catch (Exception e1) {
							stop=true;
						}
						if (!stop) {
							ris.add(tmp);
						}
					}
					stop=false;
					for (int k=i+1;k<stato.getBoard().length;k++) {
						String to = stato.getBox(k, j);
						try {
							tmp = new Action(from, to, State.Turn.WHITE);
							rules.checkMove(stato, tmp);
						} catch (Exception e1) {
							stop=true;
						}
						if (!stop) {
							ris.add(tmp);
						}
					}
					stop=false;
					for (int k=0;k<j && !stop;k++) {
						String to = stato.getBox(i, k);
						try {
							tmp = new Action(from, to, State.Turn.WHITE);
							rules.checkMove(stato, tmp);
						} catch (Exception e1) {
							stop=true;
						}
						if (!stop) {
							ris.add(tmp);
						}
					}
					stop=false;
					for (int k=j+1;k<stato.getBoard().length && !stop;k++) {
						String to = stato.getBox(i, k);
						try {
							tmp = new Action(from, to, State.Turn.WHITE);
							rules.checkMove(stato, tmp);
						} catch (Exception e1) {
							stop=true;
						}
						if (!stop) {
							ris.add(tmp);
						}
					}				
				}
			}
		}
	}
	return ris;
}
	
	
	
	

	

}
