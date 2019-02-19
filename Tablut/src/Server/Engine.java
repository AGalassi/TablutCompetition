package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.*;

import Domain.*;
import GUI.Gui;

import com.google.gson.Gson;

/**
 * this class represent the server of the match: 2 clients with TCP connection can connect and start to play
 * @author A.Piretti
 *
 */
public class Engine {
		
	private static State state;
	private static int time;
	private static int moveCache;
	private static int gameChosen;
	private static String theGson;
	private static Action move;
	private static int errors;
		
	private ServerSocket socketWhite;
	private ServerSocket socketBlack;
	
	private int blackErrors;
	private int whiteErrors;
	
	private Game game;
	private Gson gson;
	private Gui theGui;
	private int gameC;
	
	public Engine(int timeout, int cacheSize, int numErrors, int game) {
		this.gameC=game;
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
			this.game = new GameAshtonTablut(moveCache);
			break;	
		default:
			System.out.println("Error in game selection");
			System.exit(4);
		}
		
		
		Engine.time = timeout;
		Engine.moveCache = cacheSize;
		Engine.errors = numErrors;		
		
		this.gson = new Gson();
		theGui = new Gui(this.gameC);
		theGui.update(state);
		
	}
	
	/**
	 * this is the main that is launched. It creates the engine that starts the match
	 * 
	 * @param args 
	 * 		we have in order: the time for the move, cache, errors, type of game
	 * 
	 */
	public static void main(String[] args)
	{
		if(args.length == 4) 
		{
			try
			{
				time = Integer.parseInt(args[0]);
				if (time < 1) 
				{
					System.out.println("Time format not allowed!");
					System.exit(1);
				}
			}
			catch(Exception e)
			{
				System.out.println("The time format is not correct!");
				System.exit(1);
			}
			try
			{
				moveCache = Integer.parseInt(args[1]);
				if (moveCache < 1)
				{
					System.out.println("Move number is not correct!");
					System.exit(1);
				}
			}
			catch(Exception e)
			{
				System.out.println("Number format is not correct!");
				System.exit(1);
			}
			try
			{
				errors = Integer.parseInt(args[2]);
				if (errors < 0) 
				{
					System.out.println("Error format not allowed!");
					System.exit(1);
				}
			}
			catch(Exception e)
			{
				System.out.println("The error format is not correct!");
				System.exit(1);
			}
			try
			{
				gameChosen = Integer.parseInt(args[3]);
				if (gameChosen < 0 || gameChosen>4) 
				{
					System.out.println("Error format not allowed!");
					System.exit(1);
				}
			}
			catch(Exception e)
			{
				System.out.println("The error format is not correct!");
				System.exit(1);
			}
		} 
		else 
		{
			System.out.println("Usage: java ENGINE <time> <cache> <errors> <game>");
			System.exit(1);
		}
		
		//LANCIO IL MOTORE PER UN SERVER
		Engine engine = new Engine(time, moveCache, errors, gameChosen);
		
		engine.run();
		
	}
	
	/**
	 * This class represents the stream who is waiting for the move from the client (JSON format)
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
				theGson = this.theStream.readUTF();
				
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 *  This method starts the proper game.
	 *  It waits the connections from 2 clients, check the move and update the state.
	 *  There is a timeout of 5 hours to wait the connections.
	 */
	public void run()
	{
		String sysLogName = (new Date().getTime())+"_systemLog.txt";
		@SuppressWarnings("unused")
		File systemLog = new File(sysLogName);
		FileHandler fh = null;
		try
		{
			fh = new FileHandler(sysLogName, true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		Logger loggSys = Logger.getLogger("SysLog");
		loggSys.addHandler(fh);
		fh.setFormatter(new SimpleFormatter());
		loggSys.setLevel(Level.FINE);
		loggSys.fine("Accensione server");
		switch (gameC) {
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
		Socket white=null;
		Socket black=null;
		DataInputStream whiteMove=null;
		DataInputStream blackMove=null;
		DataOutputStream whiteState=null;
		DataOutputStream blackState=null;
		System.out.println("Waiting for connections...");
		try
		{
			
			this.socketWhite = new ServerSocket(5800);
			this.socketBlack = new ServerSocket(5801);
			
			white = this.socketWhite.accept();
			loggSys.fine("Accettata connessione con client giocatore Bianco");
			whiteMove = new DataInputStream(white.getInputStream());
			whiteState = new DataOutputStream(white.getOutputStream());
			black= this.socketBlack.accept();
			loggSys.fine("Accettata connessione con client giocatore Nero");
			blackMove = new DataInputStream(black.getInputStream());
			blackState = new DataOutputStream(black.getOutputStream());
			
		}
		catch(IOException e)
		{
			System.out.println("Socket error....");
			loggSys.warning("Errore connessioni");
			loggSys.warning("Chiusura sistema");
			System.exit(1);
		}
		
		System.out.println("Clients connected..");
		TCPInput tin = null;
		TCPInput Turnwhite = new TCPInput(whiteMove);
		TCPInput Turnblack = new TCPInput(blackMove);
		
		try
		{
			theGson = gson.toJson(state);
			whiteState.writeUTF(theGson);
			blackState.writeUTF(theGson);
			loggSys.fine("Invio messaggio ai giocatori");
			theGui.update(state);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			loggSys.fine("Errore invio messaggio ai giocatori");
			loggSys.warning("Chiusura sistema");
			System.exit(1);
		}	
		
		while(true)
		{
			//System.out.println("State: \n"+state.toString());
			System.out.println("Waiting for " + state.getTurn() + "...");
			Date ti = new Date();
			long hoursoccurred = (ti.getTime() - starttime.getTime()) / 60 / 60 / 1000;
			if (hoursoccurred > 5) {
				System.out.println("TIMEOUT! END OF THE GAME...");
				loggSys.warning("Chiusura programma per timeout di cinque ore");
			}
			
			switch (state.getTurn()) {
			case WHITE:
				tin = Turnwhite;
				break;
			case BLACK:
				tin = Turnblack;
				break;
			case BLACKWIN:
				break;
			case WHITEWIN:
				break;
			case DRAW:
				break;
			default:
				loggSys.warning("Chiusura sistema per errore turno");
				System.exit(4);
			}
			t = new Thread(tin);
			t.start();
			loggSys.fine("Lettura mossa player "+state.getTurn()+" in corso..");
			try 
			{
				int counter = 0;
				while (counter < time && t.isAlive()) 
				{
					Thread.sleep(1000);
					counter++;
				}
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			if (t.isAlive()) 
			{
				System.out.println("Timeout!!!!");
				System.out.println("Player " + state.getTurn().toString() + " has lost!");
				loggSys.warning("Timeout! Player "+state.getTurn()+" lose!");
				loggSys.warning("Chiusura sistema per timeout");
				System.exit(0);
			}
			
			move = this.gson.fromJson(theGson, Action.class);
			
			try
			{
				// aggiorna tutto e determina anche eventuali fine partita
				state = this.game.checkMove(state, move);
			}
			catch(Exception e)
			{
				if(state.getTurn().equalsTurn("B"))
				{
					this.blackErrors++;
					
					if(this.blackErrors>errors)
					{
						System.out.println("TOO MUCH ERRORS FOR BLACK PLAYER; PLAYER WHITE WIN!");
						e.printStackTrace();
						loggSys.warning("Chiusura sistema per troppi errori giocatore nero");
						System.exit(1);
					}
					else
					{
						System.out.println("Error for black player...");
					}
				}
				if(state.getTurn().equalsTurn("W"))
				{
					this.whiteErrors++;
					if(this.whiteErrors>errors)
					{
						System.out.println("TOO MUCH ERRORS FOR WHITE PLAYER; PLAYER BLACK WIN!");
						e.printStackTrace();
						loggSys.warning("Chiusura sistema per troppi errori giocatore bianco");
						System.exit(1);
					}
					else
					{
						System.out.println("Error for white player...");
					}
				}				
			}
						
			
			try
			{
				theGson = gson.toJson(state);
				whiteState.writeUTF(theGson);
				blackState.writeUTF(theGson);
				loggSys.fine("Invio messaggio ai client");
				theGui.update(state);
			}
			catch(IOException e)
			{
				e.printStackTrace();
				loggSys.warning("Errore invio messaggio ai client");
				loggSys.warning("Chiusura sistema");
				System.exit(1);
			}	
			
			if(!state.getTurn().equalsTurn("W") && !state.getTurn().equalsTurn("B"))
			{
				System.out.println("END OF THE GAME");
				if(state.getTurn().equalsTurn(StateTablut.Turn.DRAW.toString()))
				{
					System.out.println("RESULT: DRAW");
				}
				if(state.getTurn().equalsTurn(StateTablut.Turn.WHITEWIN.toString()))
				{
					System.out.println("RESULT: PLAYER WHITE WIN");
				}
				if(state.getTurn().equalsTurn(StateTablut.Turn.BLACKWIN.toString()))
				{
					System.out.println("RESULT: PLAYER BLACK WIN");
				}
				System.exit(0);
			}
		}
	}
	
	

}
