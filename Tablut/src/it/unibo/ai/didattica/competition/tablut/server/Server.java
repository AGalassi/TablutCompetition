package it.unibo.ai.didattica.competition.tablut.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.*;

import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.gui.Gui;
import it.unibo.ai.didattica.competition.tablut.util.Configuration;
import it.unibo.ai.didattica.competition.tablut.util.StreamUtils;

import com.google.gson.Gson;
import org.apache.commons.cli.*;

/**
 * this class represent the server of the match: 2 clients with TCP connection
 * can connect and start to play
 * 
 * @author A.Piretti, Andrea Galassi
 *
 */
public class Server implements Runnable {
	
	/**
	 * Timeout for waiting for a client to connect
	 */
	public static int connectionTimeout = 300;

	/**
	 * State of the game
	 */
	private State state;
	/**
	 * Number of seconds allowed for a decision
	 */
	private int time;
	/**
	 * Number of states kept in memory for the detection of a draw
	 */
	private int moveCache;
	/**
	 * Whether the gui must be enabled or not
	 */
	private boolean enableGui;

	/**
	 * JSON string used to communicate
	 */
	private String theGson;
	/**
	 * Action chosen by a player
	 */
	private Action move;
	/**
	 * Errors allowed
	 */
	private int errors;
	/**
	 * Repeated positions allowed
	 */
	private int repeated;

	private ServerSocket socketWhite;
	private ServerSocket socketBlack;

	private Socket white;
	private Socket black;

	/**
	 * Counter for the errors of the black player
	 */
	private int blackErrors;
	/**
	 * Counter for the errors of the white player
	 */
	private int whiteErrors;

	private int cacheSize;

	private Game game;
	private Gson gson;
	private Gui theGui;
	/**
	 * Integer that represents the game type
	 */
	private int gameC;

	public Server(int timeout, int cacheSize, int numErrors, int repeated, int game, boolean gui) {
		this.gameC = game;
		this.enableGui = gui;
		this.time = timeout;
		this.moveCache = cacheSize;
		this.errors = numErrors;
		this.cacheSize = cacheSize;
		this.gson = new Gson();
	}

	public void initializeGUI(State state) {
		this.theGui = new Gui(this.gameC);
		this.theGui.update(state);
	}

	/**
	 * Server initialiazer.
	 * 
	 * @param args
	 *            the time for the move, the size of the cache for monitoring
	 *            draws, the number of errors allowed, the type of game, whether
	 *            the GUI should be used or not
	 * 
	 */
	public static void main(String[] args) {
		int time = 60;
		int moveCache = -1;
		int repeated = 0;
		int errors = 0;
		int gameChosen = 4;
		boolean enableGui = true;

		CommandLineParser parser = new DefaultParser();

		Options options = new Options();

		options.addOption("t","time", true, "time must be an integer (number of seconds); default: 60");
		options.addOption("c", "cache", true, "cache must be an integer, negative value means infinite; default: infinite");
		options.addOption("e", "errors", true, "errors must be an integer >= 0; default: 0");
		options.addOption("s", "repeatedState", true, "repeatedStates must be an integer >= 0; default: 0");
		options.addOption("r","game rules", true, "game rules must be an integer; 1 for Tablut, 2 for Modern, 3 for Brandub, 4 for Ashton; default: 4");
		options.addOption("g","enableGUI", false, "enableGUI if option is present");

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java Server", options);

		try{
			CommandLine cmd = parser.parse( options, args );
			if (cmd.hasOption("t")){
				String timeInsert = cmd.getOptionValue("t");
				try{
					time = Integer.parseInt(timeInsert);
					if(time<1){
						System.out.println("Time format not allowed!");
						formatter.printHelp("java Server", options);
						System.exit(1);
					}
				}catch (NumberFormatException e){
					System.out.println("The time format is not correct!");
					formatter.printHelp("java Server", options);
					System.exit(1);
				}
			}
			if (cmd.hasOption("c")){
				String moveCacheInsert = cmd.getOptionValue("c");
				try{
					moveCache = Integer.parseInt(moveCacheInsert);
				}catch (NumberFormatException e){
					System.out.println("Number format is not correct!");
					formatter.printHelp("java Server", options);
					System.exit(1);
				}
			}
			if (cmd.hasOption("e")){
				try{
					errors = Integer.parseInt(cmd.getOptionValue("e"));
				}catch (NumberFormatException e) {
					System.out.println("The error format is not correct!");
					formatter.printHelp("java Server", options);
					System.exit(1);
				}
			}
			if (cmd.hasOption("s")){
				try{
					repeated = Integer.parseInt(cmd.getOptionValue("s"));
					if (repeated<0){
						System.out.println("he RepeatedStates value is not allowed!");
						formatter.printHelp("java Server", options);
						System.exit(1);
					}
				}catch (NumberFormatException e){
					System.out.println("The RepeatedStates format is not correct!");
					formatter.printHelp("java Server", options);
					System.exit(1);
				}
			}

			if(cmd.hasOption("r")){
				try{
					gameChosen = Integer.parseInt(cmd.getOptionValue("r"));
					if (gameChosen < 0 || gameChosen > 4){
						System.out.println("Game format not allowed!");
						formatter.printHelp("java Server", options);
						System.exit(1);
					}
				}catch (NumberFormatException e){
					System.out.println("The game format is not correct!");
					formatter.printHelp("java Server", options);
					System.exit(1);
				}
			}

			if(cmd.hasOption("g")){
				enableGui=true;
			}else{
				enableGui=false;
			}

		}catch (ParseException exp){
			System.out.println( "Unexpected exception:" + exp.getMessage() );
		}

		// Start the server
		Server engine = new Server(time, moveCache, errors, repeated, gameChosen, enableGui);
		engine.run();
	}

	/**
	 * This class represents the stream who is waiting for the move from the
	 * client (JSON format)
	 * 
	 * @author A.Piretti
	 *
	 */
	private class TCPInput implements Runnable {
		private DataInputStream theStream;

		public TCPInput(DataInputStream theS) {
			this.theStream = theS;
		}

		public void run() {
			try {
				theGson = StreamUtils.readString(this.theStream);

			} catch (Exception e) {
			}
		}
	}

	/**
	 * This class represents the socket waiting for a connection
	 * @author Andrea Galassi
	 *
	 */
	private class TCPConnection implements Runnable {
		private ServerSocket serversocket;
		private Socket socket;

		public TCPConnection(ServerSocket serverSocket) {
			this.serversocket = serverSocket;
		}

		public void run() {
			try {
				socket = serversocket.accept();
			} catch (Exception e) {
			}
		}

		public Socket getSocket() {
			return socket;
		}
	}

	/**
	 * This method starts the proper game. It waits the connections from 2
	 * clients, check the move and update the state. There is a timeout that
	 * interrupts games that last too much
	 */
	public void run() {
		/**
		 * Number of hours that a game can last before the timeout
		 */
		int hourlimit = 10;
		/**
		 * Endgame state reached?
		 */
		boolean endgame = false;
		/**
		 * Name of the systemlog
		 */
		String logs_folder = "logs";
		Path p = Paths.get(logs_folder + File.separator + new Date().getTime() + "_systemLog.txt");
		p = p.toAbsolutePath();
		String sysLogName = p.toString();
		Logger loggSys = Logger.getLogger("SysLog");
		try {
			new File(logs_folder).mkdirs();
			System.out.println(sysLogName);
			File systemLog = new File(sysLogName);
			if (!systemLog.exists()) {
				systemLog.createNewFile();
			}
			FileHandler fh = null;
			fh = new FileHandler(sysLogName, true);
			loggSys.addHandler(fh);
			fh.setFormatter(new SimpleFormatter());
			loggSys.setLevel(Level.FINE);
			loggSys.fine("Accensione server");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		switch (this.gameC) {
		case 1:
			loggSys.fine("Partita di ClassicTablut");
			break;
		case 2:
			loggSys.fine("Partita di ModernTablut");
			break;
		case 3:
			loggSys.fine("Partita di Brandub");
			break;
		case 4:
			loggSys.fine("Partita di Tablut");
			break;
		default:
			System.out.println("Error in game selection");
			System.exit(4);
		}

		Date starttime = new Date();
		Thread t;

		/**
		 * Channel to receive the move of the white player
		 */
		DataInputStream whiteMove = null;
		/**
		 * Channel to receive the move of the black player
		 */
		DataInputStream blackMove = null;
		/**
		 * Channel to send the state to the white player
		 */
		DataOutputStream whiteState = null;
		/**
		 * Channel to send the state to the black player
		 */
		DataOutputStream blackState = null;
		System.out.println("Waiting for connections...");

		String whiteName = "WP";
		String blackName = "BP";

		/**
		 * Socket of the current player
		 */
		TCPInput tin = null;
		TCPInput Turnwhite = null;
		TCPInput Turnblack = null;
		TCPConnection tc = null;

		// ESTABLISH CONNECTIONS AND NAME READING
		try {
			this.socketWhite = new ServerSocket(Configuration.whitePort);
			this.socketBlack = new ServerSocket(Configuration.blackPort);
			

			// ESTABLISHING CONNECTION
			tc = new TCPConnection(socketWhite);
			t = new Thread(tc);
			t.start();
			loggSys.fine("Waiting for white connection..");
			// timeout for connection
			try {
				int counter = 0;
				while (counter < connectionTimeout && t.isAlive()) {
					Thread.sleep(1000);
					counter++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (t.isAlive()) {
				System.out.println("Timeout!!!!");
				loggSys.warning("Closing system for timeout!");
				System.exit(0);
			}
			
			white = tc.getSocket();
			loggSys.fine("White player connected");
			whiteMove = new DataInputStream(white.getInputStream());
			whiteState = new DataOutputStream(white.getOutputStream());
			Turnwhite = new TCPInput(whiteMove);

			// NAME READING
			t = new Thread(Turnwhite);
			t.start();
			loggSys.fine("Lettura nome player bianco in corso..");
			// timeout for name declaration
			try {
				int counter = 0;
				while (counter < time && t.isAlive()) {
					Thread.sleep(1000);
					counter++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (t.isAlive()) {
				System.out.println("Timeout!!!!");
				loggSys.warning("Chiusura sistema per timeout");
				System.exit(0);
			}

			whiteName = this.gson.fromJson(theGson, String.class);
			// SECURITY STEP: dropping unproper characters
			String temp = "";
			for (int i = 0; i < whiteName.length() && i < 10; i++) {
				char c = whiteName.charAt(i);
				if (Character.isAlphabetic(c) || Character.isDigit(c))
					temp += c;
			}
			whiteName = temp;
			System.out.println("White player name:\t" + whiteName);
			loggSys.fine("White player name:\t" + whiteName);

			
			// ESTABLISHING CONNECTION
			tc = new TCPConnection(socketBlack);
			t = new Thread(tc);
			t.start();
			loggSys.fine("Waiting for Black connection..");
			// timeout for connection
			try {
				int counter = 0;
				while (counter < connectionTimeout && t.isAlive()) {
					Thread.sleep(1000);
					counter++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (t.isAlive()) {
				System.out.println("Timeout!!!!");
				loggSys.warning("Closing system for timeout!");
				System.exit(0);
			}
			black = tc.getSocket();
			loggSys.fine("Accettata connessione con client giocatore Nero");
			blackMove = new DataInputStream(black.getInputStream());
			blackState = new DataOutputStream(black.getOutputStream());
			Turnblack = new TCPInput(blackMove);

			// NAME READING
			t = new Thread(Turnblack);
			t.start();
			loggSys.fine("Lettura nome player nero in corso..");
			try {
				// timer for the move
				int counter = 0;
				while (counter < time && t.isAlive()) {
					Thread.sleep(1000);
					counter++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// timeout for name declaration
			if (t.isAlive()) {
				System.out.println("Timeout!!!!");
				loggSys.warning("Chiusura sistema per timeout");
				System.exit(0);
			}

			blackName = this.gson.fromJson(theGson, String.class);
			// SECURITY STEP: dropping unproper characters
			temp = "";
			for (int i = 0; i < blackName.length() && i < 10; i++) {
				char c = blackName.charAt(i);
				if (Character.isAlphabetic(c) || Character.isDigit(c))
					temp += c;
			}
			System.out.println("Black player name:\t" + blackName);
			loggSys.fine("Black player name:\t" + blackName);
			blackName = temp;

		} catch (IOException e) {
			System.out.println("Socket error....");
			loggSys.warning("Errore connessioni");
			loggSys.warning("Chiusura sistema");
			System.exit(1);
		}

		switch (this.gameC) {
		case 1:
			state = new StateTablut();
			this.game = new GameTablut(moveCache);
			break;
		case 2:
			state = new StateTablut();
			this.game = new GameModernTablut(moveCache);
			break;
		case 3:
			state = new StateBrandub();
			this.game = new GameTablut(moveCache);
			break;
		case 4:
			state = new StateTablut();
			state.setTurn(State.Turn.WHITE);
			this.game = new GameAshtonTablut(state, repeated, this.cacheSize, "logs", whiteName, blackName);
			break;
		default:
			System.out.println("Error in game selection");
			System.exit(4);
		}
		if (this.enableGui) {
			this.initializeGUI(state);
		}
		System.out.println("Clients connected..");

		// SEND INITIAL STATE

		tin = Turnwhite;
		try {
			theGson = gson.toJson(state);
			StreamUtils.writeString(whiteState, theGson);
			StreamUtils.writeString(blackState, theGson);
			loggSys.fine("Invio messaggio ai giocatori");
			if (enableGui) {
				theGui.update(state);
			}
		} catch (IOException e) {
			e.printStackTrace();
			loggSys.fine("Errore invio messaggio ai giocatori");
			loggSys.warning("Chiusura sistema");
			System.exit(1);
		}

		// GAME CYCLE
		while (!endgame) {
			// RECEIVE MOVE

			// System.out.println("State: \n"+state.toString());
			System.out.println("Waiting for " + state.getTurn() + "...");

			// create the process that listen the answer
			t = new Thread(tin);
			t.start();
			loggSys.fine("Lettura mossa player " + state.getTurn() + " in corso..");
			try {
				// timer for the move
				int counter = 0;
				while (counter < time && t.isAlive()) {
					Thread.sleep(1000);
					counter++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// loss for timeout
			if (t.isAlive()) {
				System.out.println("Timeout!!!!");
				System.out.println("Player " + state.getTurn().toString() + " has lost!");
				loggSys.warning("Timeout! Player " + state.getTurn() + " lose!");
				loggSys.warning("Chiusura sistema per timeout");
				System.exit(0);
			}

			// APPLY MOVE
			// translate the string into an action object
			move = this.gson.fromJson(theGson, Action.class);
			loggSys.fine("Move received.\t" + move.toString());
			move.setTurn(state.getTurn());
			System.out.println("Suggested move: " + move.toString());

			try {
				// aggiorna tutto e determina anche eventuali fine partita
				state = this.game.checkMove(state, move);
			} catch (Exception e) {
				// exception means error, therefore increase the error counters
				if (state.getTurn().equalsTurn("B")) {
					this.blackErrors++;

					if (this.blackErrors > errors) {
						System.out.println("TOO MANY ERRORS FOR BLACK PLAYER; PLAYER WHITE WIN!");
						e.printStackTrace();
						loggSys.warning("Chiusura sistema per troppi errori giocatore nero");
						state.setTurn(Turn.WHITEWIN);
						this.game.endGame(state);
					} else {
						System.out.println("Error for black player...");
					}
				}
				if (state.getTurn().equalsTurn("W")) {
					this.whiteErrors++;
					if (this.whiteErrors > errors) {
						System.out.println("TOO MANY ERRORS FOR WHITE PLAYER; PLAYER BLACK WIN!");
						e.printStackTrace();
						loggSys.warning("Chiusura sistema per troppi errori giocatore bianco");
						state.setTurn(Turn.BLACKWIN);
						this.game.endGame(state);
					} else {
						System.out.println("Error for white player...");
					}
				}
			}

			// TODO: in case of more errors allowed, it is fair to send the same
			// state once again?
			// In case not, the client should always read and act when is their
			// turn

			// GAME TOO LONG, TIMEOUT
			Date ti = new Date();
			long hoursoccurred = (ti.getTime() - starttime.getTime()) / 60 / 60 / 1000;
			if (hoursoccurred > hourlimit) {
				System.out.println("TIMEOUT! END OF THE GAME...");
				loggSys.warning("Chiusura programma per timeout di " + hourlimit + " ore");
				state.setTurn(Turn.DRAW);
			}

			// SEND STATE TO PLAYERS
			try {
				theGson = gson.toJson(state);
				StreamUtils.writeString(whiteState, theGson);
				StreamUtils.writeString(blackState, theGson);
				loggSys.fine("Invio messaggio ai client");
				if (enableGui) {
					theGui.update(state);
				}
			} catch (IOException e) {
				e.printStackTrace();
				loggSys.warning("Errore invio messaggio ai client");
				loggSys.warning("Chiusura sistema");
				System.exit(1);
			}


			switch (state.getTurn()) {
			case WHITE:
				tin = Turnwhite;
				break;
			case BLACK:
				tin = Turnblack;
				break;
			case BLACKWIN:
				this.game.endGame(state);
				System.out.println("END OF THE GAME");
				System.out.println("RESULT: PLAYER BLACK WIN");
				endgame = true;
				break;
			case WHITEWIN:
				this.game.endGame(state);
				System.out.println("END OF THE GAME");
				System.out.println("RESULT: PLAYER WHITE WIN");
				endgame = true;
				break;
			case DRAW:
				this.game.endGame(state);
				System.out.println("END OF THE GAME");
				System.out.println("RESULT: DRAW");
				endgame = true;
				break;
			default:
				loggSys.warning("Chiusura sistema");
				System.exit(4);
			}

		}
		
		System.exit(0);
	}

}
