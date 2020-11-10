package it.unibo.ai.didattica.competition.tablut.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.GameModernTablut;
import it.unibo.ai.didattica.competition.tablut.domain.GameTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateBrandub;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.server.Server;

public class PlayerAI extends TablutClient {

	
	public PlayerAI(String player, String name, int timeout, String ipAddress)
			throws UnknownHostException, IOException {
		super(player, name, timeout, ipAddress);
		// TODO Auto-generated constructor stub
	}

	public PlayerAI(String player, String name, int timeout) throws UnknownHostException, IOException {
		super(player, name, timeout);
		// TODO Auto-generated constructor stub
	}

	public PlayerAI(String player, String name, String ipAddress) throws UnknownHostException, IOException {
		super(player, name, ipAddress);
		// TODO Auto-generated constructor stub
	}

	public PlayerAI(String player, String name) throws UnknownHostException, IOException {
		super(player, name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		State state;

		Game rules = null;
		
		state = new StateTablut();
		state.setTurn(State.Turn.WHITE);
		rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
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
			
			state = this.getCurrentState();
			this.get
			if (this.getPlayer().equals(state.getTurn())) {
				//sta a me		
			} else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)){
				System.out.println("Vince il bianco");
				System.exit(0);
			} else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)){
				System.out.println("Vince il nero");
				System.exit(0);
			} else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
				System.out.println("DRAW!");
				System.exit(0);
			} else {
				System.out.println("Sta all'avversario");
			}
		}

	}

	@Override
	public Turn getPlayer() {
		// TODO Auto-generated method stub
		return super.getPlayer();
	}

	@Override
	public void setPlayer(Turn player) {
		// TODO Auto-generated method stub
		super.setPlayer(player);
	}

	@Override
	public State getCurrentState() {
		// TODO Auto-generated method stub
		return super.getCurrentState();
	}

	@Override
	public void setCurrentState(State currentState) {
		// TODO Auto-generated method stub
		super.setCurrentState(currentState);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return super.getName();
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		super.setName(name);
	}

	@Override
	public void write(Action action) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		super.write(action);
	}

	@Override
	public void declareName() throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		super.declareName();
	}

	@Override
	public void read() throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		super.read();
	}

	

	

	

}
