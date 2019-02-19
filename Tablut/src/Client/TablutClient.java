package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.gson.Gson;

import Domain.Action;
import Domain.State;
import Domain.StateTablut;

/**
 * Classe astratta di un client per il gioco Tablut
 * 
 * @author Andrea Piretti
 *
 */
public abstract class TablutClient {
	
	private State.Turn player;
	private Socket playerSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private Gson gson;
	private State currentState;
	
	public State.Turn getPlayer() {
		return player;
	}

	public void setPlayer(State.Turn player) {
		this.player = player;
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	public TablutClient(String player) throws UnknownHostException, IOException {
		int port = 0;
		this.gson= new Gson();
		switch (player) {
		case "WHITE":
			this.player = State.Turn.WHITE;
			port = 5800;
			break;
		case "BLACK":
			this.player = State.Turn.BLACK;
			port = 5801;
			break;
		default:
			System.exit(5);
		}
		playerSocket = new Socket("localhost", port);
		out = new DataOutputStream(playerSocket.getOutputStream());
		in = new DataInputStream(playerSocket.getInputStream());
	}
	
	/**
	 * Write an action to the server
	 */
	public void write(Action action) throws IOException, ClassNotFoundException {
		out.writeUTF(this.gson.toJson(action));
	}
	
	/**
	 * Read the state from the server
	 */
	public void read() throws ClassNotFoundException, IOException {
		this.currentState = this.gson.fromJson(in.readUTF(), StateTablut.class);
	}
}
