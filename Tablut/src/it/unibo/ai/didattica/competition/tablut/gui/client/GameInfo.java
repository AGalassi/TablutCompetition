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
	public static final String DEFAULT_SERVER_IP = "localhost";
	
	private String username = DEFAULT_USERNAME;
	private State.Turn side = DEFAULT_SIDE;
	private int timeout = DEFAULT_TIMEOUT;
	private String serverIP = DEFAULT_SERVER_IP;
	
	public GameInfo() {
		this(DEFAULT_SIDE, DEFAULT_USERNAME + StringUtils.capitalize(DEFAULT_SIDE.name()), DEFAULT_TIMEOUT, DEFAULT_SERVER_IP);
	}
	public GameInfo(String side) {
		this(StringUtils.parseSide(side), DEFAULT_USERNAME + side, DEFAULT_TIMEOUT, DEFAULT_SERVER_IP);
	}
	public GameInfo(String side, String username) {
		this(StringUtils.parseSide(side), username, DEFAULT_TIMEOUT, DEFAULT_SERVER_IP);
	}
	public GameInfo(State.Turn side, String username) {
		this(side, username, DEFAULT_TIMEOUT, DEFAULT_SERVER_IP);
	}
	public GameInfo(State.Turn side, String username, String timeout, String serverIP) {
		this(side, username, StringUtils.parseInteger(timeout), serverIP);
	}
	public GameInfo(String side, String username, String timeout, String serverIP) {
		this(StringUtils.parseSide(side), username, StringUtils.parseInteger(timeout), serverIP);
	}
	public GameInfo(String side, String username, int timeout, String serverIP) {
		this(StringUtils.parseSide(side), username, timeout, serverIP);
	}
	public GameInfo(State.Turn side, String username, int timeout, String serverIP) {
		if(!validateSide(side)) {
			side = DEFAULT_SIDE;
		}
		if(!validateUsername(username) && !username.trim().isEmpty()) {
			username = DEFAULT_USERNAME + getSideString();
		}
		if(!validateTimeout(timeout)) {
			timeout = DEFAULT_TIMEOUT;
		}
		if(!validateServerAddress(serverIP) && !serverIP.isEmpty()) {
			serverIP = DEFAULT_SERVER_IP;
		}
		this.side = side;
		this.username = username;
		this.timeout = timeout;
		this.serverIP = serverIP;
	}
	
	public State.Turn getSide() {
		return side;
	}
	public State.Turn getOpponentSide() {
		return side.equals(State.Turn.BLACK) ? State.Turn.WHITE : State.Turn.BLACK;
	}
	public void setSide(State.Turn side) {
		if(!validateSide(side)) {
			side = DEFAULT_SIDE;
		} else {
			this.side = side;
		}
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		if(!validateUsername(username) && !username.trim().isEmpty()) {
			username = DEFAULT_USERNAME;
		} else {
			this.username = username;
		}
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		if(!validateTimeout(timeout)) {
			timeout = DEFAULT_TIMEOUT;
		} else {
			this.timeout = timeout;
		}
	}
	public String getServerIP() {
		return serverIP;
	}
	public void setServerIP(String serverIP) {
		if(!validateServerAddress(serverIP) && !serverIP.isEmpty()) {
			serverIP = DEFAULT_SERVER_IP;
		} else {
			this.serverIP = serverIP;
		}
	}
	
	public String getSideString() {
		return StringUtils.capitalize(side.name());
	}
	public String getOpponentSideString() {
		return StringUtils.capitalize(getOpponentSide().name());
	}
	public static boolean validateSide(State.Turn side) {
		return side.equals(State.Turn.WHITE) || side.equals(State.Turn.BLACK);
	}
	public static boolean validateSide(String side) {
		return side.equalsIgnoreCase("White") || side.equalsIgnoreCase("Black");
	}
	public static boolean validateUsername(String username) {
		return PATTERN_USERNAME.matcher(username.trim()).matches();
	}
	public static boolean validateTimeout(int timeout) {
		return MIN_TIMEOUT <= timeout && timeout <= MAX_TIMEOUT;
	}
	public static boolean validateTimeout(String timeout) {
		return StringUtils.isNumeric(timeout) && MIN_TIMEOUT <= StringUtils.parseInteger(timeout) && StringUtils.parseInteger(timeout) <= MAX_TIMEOUT;
	}
	public static boolean validateServerAddress(String address) {
		return PATTERN_IP.matcher(address).matches() || address.equals("localhost");
	}
}