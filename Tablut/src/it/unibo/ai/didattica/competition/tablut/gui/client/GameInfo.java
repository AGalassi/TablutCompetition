package it.unibo.ai.didattica.competition.tablut.gui.client;

import java.util.regex.Pattern;

import it.unibo.ai.didattica.competition.tablut.domain.State;

/**
 * Class to hold game info needed to connect to the game server.
 * 
 * @author Michele Righi
 * (<a href="https://github.com/mikyll">GitHub</a>,
 * <a href="https://www.linkedin.com/in/michele-righi/">LinkedIn</a>)
 */
public class GameInfo {
	public static final Pattern PATTERN_IP = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	public static final Pattern PATTERN_USERNAME = Pattern.compile("^[a-zA-Z0-9]{3,20}$");
	public static final int MIN_TIMEOUT = 5;
	public static final int MAX_TIMEOUT = 120;
	public static final int TIMEOUT_INCREMENT = 5;
	
	public static final String DEFAULT_USERNAME = "Player";
	public static final State.Turn DEFAULT_SIDE = State.Turn.WHITE;
	public static final int DEFAULT_TIMEOUT = 60;
	public static final String DEFAULT_SERVER_IP = "127.0.0.1";
	
	private String username = DEFAULT_USERNAME;
	private State.Turn side = DEFAULT_SIDE;
	private int timeout = DEFAULT_TIMEOUT;
	private String serverIP = DEFAULT_SERVER_IP;
	
	public GameInfo() {
		this(DEFAULT_USERNAME + DEFAULT_SIDE.name().substring(0,1).toUpperCase() + DEFAULT_SIDE.name().substring(1).toLowerCase(),
				DEFAULT_SIDE, DEFAULT_TIMEOUT, DEFAULT_SERVER_IP);
	}
	public GameInfo(String username) {
		this(username, DEFAULT_SIDE, DEFAULT_TIMEOUT, DEFAULT_SERVER_IP);
	}
	public GameInfo(String username, String side) {
		this(username, side.equalsIgnoreCase("White") ? State.Turn.WHITE : State.Turn.BLACK, DEFAULT_TIMEOUT, DEFAULT_SERVER_IP);
	}
	public GameInfo(String username, State.Turn side) {
		this(username, side, DEFAULT_TIMEOUT, DEFAULT_SERVER_IP);
	}
	public GameInfo(String username, String side, int timeout, String serverIP) {
		this(username, side.equalsIgnoreCase("White") ? State.Turn.WHITE : State.Turn.BLACK, timeout, serverIP);
	}
	public GameInfo(String username, State.Turn side, int timeout, String serverIP) {
		if(username.length() < 3 || username.length() > 20) {
			username = DEFAULT_USERNAME;
		}
		if(!side.equals(State.Turn.WHITE) && !side.equals(State.Turn.BLACK)) {
			side = DEFAULT_SIDE;
		}
		if(timeout < MIN_TIMEOUT || timeout > MAX_TIMEOUT) {
			timeout = DEFAULT_TIMEOUT;
		}
		if(!PATTERN_IP.matcher(serverIP.trim()).matches() && !serverIP.trim().isEmpty()) {
			serverIP = DEFAULT_SERVER_IP;
		}
		this.username = username;
		this.side = side;
		this.timeout = timeout;
		this.serverIP = serverIP;
	}
	
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		if(username.length() < 3 || username.length() > 20) {
			username = DEFAULT_USERNAME;
		} else {
			this.username = username;
		}
	}
	public State.Turn getSide() {
		return side;
	}
	public State.Turn getOpponentSide() {
		return side.equals(State.Turn.BLACK) ? State.Turn.WHITE : State.Turn.BLACK;
	}
	public void setSide(State.Turn side) {
		if(!side.equals(State.Turn.WHITE) && !side.equals(State.Turn.BLACK)) {
			side = DEFAULT_SIDE;
		} else {
			this.side = side;
		}
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		if(timeout < 5 || timeout > 120) {
			timeout = DEFAULT_TIMEOUT;
		} else {
			this.timeout = timeout;
		}
	}
	public String getServerIP() {
		return serverIP;
	}
	public void setServerIP(String serverIP) {
		if(!PATTERN_IP.matcher(serverIP.trim()).matches() && !serverIP.trim().isEmpty()) {
			serverIP = DEFAULT_SERVER_IP;
		} else {
			this.serverIP = serverIP;
		}
	}
	
	public String getSideString() {
		return side.name().substring(0,1).toUpperCase() + side.name().substring(1).toLowerCase();
	}
	public String getOpponentSideString() {
		return getOpponentSide().name().substring(0,1).toUpperCase() + getOpponentSide().name().substring(1).toLowerCase();
	}
}