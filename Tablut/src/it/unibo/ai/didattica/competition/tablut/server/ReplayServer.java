package it.unibo.ai.didattica.competition.tablut.server;

import it.unibo.ai.didattica.competition.tablut.domain.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replays the results of previous game
 * by loading the state from game log
 * Ideally for use with GUI
 *
 * @author Filippo Lenzi
 *
 */
public class ReplayServer extends Server {
	protected String replayFilePath;
	protected int timeBetweenTurns = 1000; // ms
	protected boolean initializedGui = false;

	public ReplayServer(String replayFilePath, int cacheSize, int numErrors, int repeated, int game, boolean gui) {
		super(Integer.MAX_VALUE, cacheSize, numErrors, repeated, game, gui);
		this.replayFilePath = replayFilePath;
	}

	@Override
	public void run() {
		try (BufferedReader reader = new BufferedReader(new FileReader(replayFilePath))) {
			parseReader(reader);
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/*
	File format:
	[...]
	mag 16, 2022 5:14:16 PM it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut checkMove
	BUONO: Turn: W Pawn from b9 to a9
	mag 16, 2022 5:14:16 PM it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut movePawn
	BUONO: Movimento pedina
	mag 16, 2022 5:14:16 PM it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut checkMove
	BUONO: Current draw cache size: 8
	mag 16, 2022 5:14:16 PM it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut checkMove
	BUONO: Stato:
	OOOOOOOOO
	OOBOOOBOO
	OBBBOOBBO
	OBOOOBOOO
	OBOOTOOOO
	OOOBOKOOO
	OBOOOOBOO
	OOOOOOBOO
	WOOOOOOOO
	-
	B
	[...]
	 */

	protected void parseReader(BufferedReader reader) throws IOException {
		final int stateNumLines = 9;
		List<String> currentStateLines = null;
		Pattern stateLinePattern = Pattern.compile("[OBWKT]{9}");
		String line;
		boolean nextIsState = false;
		int linesUntilTurn = -1;
		State.Turn lastTurn = State.Turn.WHITE;

		while ((line = reader.readLine()) != null) {
			line = line.trim();

			if (linesUntilTurn >= 0) {
				linesUntilTurn--;
				if (linesUntilTurn == 0) {
					String finalLine = line; // lambda java
					lastTurn = Arrays.stream(State.Turn.values())
							.filter(t -> t.toString().equals(finalLine))
							.findAny().get();
				}
			}

			if (nextIsState) {
				Matcher stateLineMatcher = stateLinePattern.matcher(line);
				if (stateLineMatcher.matches()) {
					currentStateLines.add(line);
					// Completed state
					if (currentStateLines.size() >= stateNumLines) {
						State state = getStateFromLines(currentStateLines, lastTurn);
						runTurn(state);
						nextIsState = false;
						linesUntilTurn = 2;
					}
				} else {
					throw new RuntimeException("Wrong file format! Expected state line, found: " + line);
				}
			}

			if (line.endsWith("Stato:")) {
				nextIsState = true;
				currentStateLines = new ArrayList<>(9);
			}
		}
	}

	protected State getStateFromLines(List<String> lines, State.Turn turn) throws IllegalStateException {
		State state;
		switch (this.gameC) {
			case 1:
			case 2:
			case 4:
				state = new StateTablut();
				break;
			case 3:
				state = new StateBrandub();
				break;
			default:
				throw new IllegalStateException("Wrong gameC somehow!");
		}
		State.Pawn[][] board = state.getBoard();

		if (lines.size() != 9)
			throw new IllegalArgumentException(String.format("Wrong number of lines: was %d, expected 9", lines.size()));

		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			if (line.length() != 9)
				throw new IllegalArgumentException(String.format("Wrong line length at line %d: was %d, expected 9", i, line.length()));
			for (int j = 0; j < line.length(); j++) {
				String pawnChar = "" + line.charAt(j);

				State.Pawn pawn = State.Pawn.fromString(pawnChar);
				board[i][j] = pawn;
			}
		}

		state.setTurn(turn);
		state.setBoard(board);

		return state;
	}

	protected void runTurn(State state) throws IOException {
		if (enableGui) {
			if (initializedGui) {
				theGui.update(state);
			} else {
				initializeGUI(state);
				initializedGui = true;
				System.out.println("Press enter to continue...");
				System.in.read();
				System.out.println("... start replay");
			}
		}
		try {
			Thread.sleep(timeBetweenTurns);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
