package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;

public class TablutHumanBlackClientGui {
	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {
		TablutHumanClientGui.main(new String[]{"Black", "BlackP", "60", "localhost"});
	}
}
