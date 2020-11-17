package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;


import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.predictor.CheckPossibleMoves;
import it.unibo.ai.didattica.competition.tablut.predictor.Predictor;
import it.unibo.ai.didattica.competition.tablut.predictor.WrapperState;

public class PlayerAI extends TablutClient {


	public PlayerAI(String player, String name, int gameChosen, int timeout, String ipAddress) throws UnknownHostException, IOException {
		super(player, name, timeout, ipAddress);
	}

	public PlayerAI(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
		this(player, name, 4, timeout, ipAddress);
	}

	public PlayerAI(String player, int timeout, String ipAddress) throws UnknownHostException, IOException {
		this(player, "tabloidi", 4, timeout, ipAddress);
	}

	public PlayerAI(String player) throws UnknownHostException, IOException {
		this(player, "tabloidi", 4, 60, "localhost");
	}

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		int gametype = 4;
		String role = "";
		String name = "tabloidi";
		String ipAddress = "localhost";
		int timeout = 60;
		// TODO: change the behavior?
		if (args.length < 1) {
			System.out.println("You must specify which player you are (WHITE or BLACK)");
			System.exit(-1);
		} else {
			System.out.println(args[0]);
			role = (args[0]);
		}
		if (args.length == 2) {
			System.out.println(args[1]);
			timeout = Integer.parseInt(args[1]);
		}
		if (args.length == 3) {
			ipAddress = args[2];
		}
		System.out.println("Selected client: " + args[0]);

		PlayerAI clientAI = new PlayerAI(role, name, gametype, timeout, ipAddress);
		clientAI.run();
	}


	public void run() {
		int turn=0;
		
		try {
			this.declareName();
		} catch (Exception e) {
			e.printStackTrace();
		}

		State state;

		CheckPossibleMoves rules = null;	
		state = new StateTablut();
		state.setTurn(State.Turn.WHITE);
		rules = new CheckPossibleMoves(99, 0, "garbage", "fake", "fake");
		System.out.println("Ashton Tablut game");

		



		System.out.println("You are player " + this.getPlayer().toString() + "!");

		while (true) {
			try {
				this.read();
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(1);
			}
			System.out.println("Current state:");
			state = this.getCurrentState();
			System.out.println(state.toString());
			WrapperState wpState = new WrapperState(state,turn);
			turn++;
			if (this.getPlayer().equals(Turn.WHITE)) {
				// Mio turno

				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {

					this.writeBestAction(wpState, rules);
				}
				// Turno dell'avversario
				else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
					System.out.println("Waiting for your opponent move... ");
				}
				// ho vinto
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				}
				// ho perso
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				}
				// pareggio
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}

			}
			else {
				// Mio turno
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
					this.writeBestAction(wpState, rules);
				}else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
					System.out.println("Waiting for your opponent move... ");
				} else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				} else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				} else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}

			}
		}

	}


	private void writeBestAction(WrapperState state, CheckPossibleMoves rules) {
		Predictor pr = new Predictor(state, rules);
		Action a = pr.findBestAction(state);
		if(a == null) {
			State stateProva = this.getCurrentState();
			Action a2 = pr.findBestAction(state);
		}
		System.out.println("Mossa scelta: " + a.toString());
		//attenzione lo state che passo deve essere lo stesso, non mi � chiaro l'initial state di Game
		try {
			this.write(a);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
