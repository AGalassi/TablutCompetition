package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.domain.*;

public class TablutRandomClient extends TablutClient {


	public TablutRandomClient(String player, int gameChosen) throws UnknownHostException, IOException {
		super(player);
	}

	public TablutRandomClient(String player) throws UnknownHostException, IOException {
		this(player, 1);
	}

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		int gametype = 4;
		// TODO: change the behavior?
		if (args.length < 1) {
			System.out.println("You must specify which player you are (WHITE or BLACK) and specify the game!");
			System.exit(-1);
		}
		if (args.length == 2) {
			System.out.println(args[0]);
			System.out.println(args[1]);
			gametype = Integer.parseInt(args[1]);
		}
		System.out.println("Selected client: " + args[0]);

		Game game = null;
		State state;
		switch (gametype) {
		case 1:
			state = new StateTablut();
			game = new GameTablut();
			break;
		case 2:
			state = new StateTablut();
			game = new GameModernTablut();
			break;
		case 3:
			state = new StateBrandub();
			game = new GameTablut();
			break;
		case 4:
			state = new StateTablut();
			state.setTurn(State.Turn.WHITE);
			game = new GameAshtonTablut(99, 0, "garbage");
			break;
		default:
			System.out.println("Error in game selection");
			System.exit(4);
		}

		TablutRandomClient client = null;
		List<int[]> pawns = new ArrayList<int[]>();
		List<int[]> empty = new ArrayList<int[]>();

		String color = args[0].toLowerCase();

		if (color.equals("white")) {
			// da modificare
			client = new TablutRandomClient("WHITE", gametype);

			System.out.println("You are player " + client.getPlayer().toString() + "!");

			while (true) {
				client.read();
				state = client.getCurrentState();
				System.out.println("Current state:");
				System.out.println(state.toString());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				// è il mio turno
				if (client.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
					int[] buf;
					for (int i = 0; i < state.getBoard().length; i++) {
						for (int j = 0; j < state.getBoard().length; j++) {
							if (state.getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())
									|| state.getPawn(i, j).equalsPawn(State.Pawn.KING.toString())) {
								buf = new int[2];
								buf[0] = i;
								buf[1] = j;
								pawns.add(buf);
							} else if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
								buf = new int[2];
								buf[0] = i;
								buf[1] = j;
								empty.add(buf);
							}
						}
					}

					int[] selected = null;

					boolean found = false;
					Action a = new Action("z0", "z0", State.Turn.WHITE);
					while (!found) {
						selected = pawns.get(new Random().nextInt(pawns.size() - 1));
						String from = client.getCurrentState().getBox(selected[0], selected[1]);

						selected = empty.get(new Random().nextInt(empty.size() - 1));
						String to = client.getCurrentState().getBox(selected[0], selected[1]);

						a = new Action(from, to, State.Turn.WHITE);

						try {
							game.checkMove(state, a);
							found = true;
						} catch (Exception e) {

						}

					}

					System.out.println("Mossa scelta: " + a.toString());
					client.write(a);
					pawns.clear();
					empty.clear();

				}
				// è il turno dell'avversario
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
		} else {
			// da modificare
			client = new TablutRandomClient("BLACK", gametype);

			System.out.println("You are player " + client.getPlayer().toString() + "!");
			while (true) {
				client.read();
				System.out.println("Current state:");
				state = client.getCurrentState();
				System.out.println(state.toString());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
				// è il mio turno
				if (client.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
					int[] buf;
					for (int i = 0; i < state.getBoard().length; i++) {
						for (int j = 0; j < state.getBoard().length; j++) {
							if (state.getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString())) {
								buf = new int[2];
								buf[0] = i;
								buf[1] = j;
								pawns.add(buf);
							} else if (state.getPawn(i, j).equalsPawn(State.Pawn.EMPTY.toString())) {
								buf = new int[2];
								buf[0] = i;
								buf[1] = j;
								empty.add(buf);
							}
						}
					}

					int[] selected = null;

					boolean found = false;
					Action a = new Action("z0", "z0", State.Turn.BLACK);
					;
					while (!found) {
						selected = pawns.get(new Random().nextInt(pawns.size() - 1));
						String from = client.getCurrentState().getBox(selected[0], selected[1]);

						selected = empty.get(new Random().nextInt(empty.size() - 1));
						String to = client.getCurrentState().getBox(selected[0], selected[1]);

						a = new Action(from, to, State.Turn.BLACK);

						System.out.println("try: " + a.toString());
						try {
							game.checkMove(state, a);
							found = true;
						} catch (Exception e) {

						}

					}

					System.out.println("Mossa scelta: " + a.toString());
					client.write(a);
					pawns.clear();
					empty.clear();

				}

				else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
					System.out.println("Waiting for your opponent move... ");
				}
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				}
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				}
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}
			}
		}
	}

}
