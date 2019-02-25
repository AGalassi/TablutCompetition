package it.unibo.ai.didattica.competition.tablut.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

public class TablutHumanClient extends TablutClient {

	public TablutHumanClient(String player) throws UnknownHostException, IOException {
		super(player);
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {

		if (args.length == 0) {
			System.out.println("You must specify which player you are (WHITE or BLACK)!");
			System.exit(-1);
		}
		System.out.println("Selected client: " + args[0]);

		String actionStringFrom = "";
		String actionStringTo="";
		Action action;
		TablutClient client = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		if ((args[0]=="WHITE")||(args[0]=="white"))
		{
			client = new TablutHumanClient("WHITE");
			System.out.println("You are player " + client.getPlayer().toString() + "!");
			while (true) 
			{
				client.read();
				System.out.println("Current state:");
				System.out.println(client.getCurrentState().toString());
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE))
				{
					System.out.println("Player " + client.getPlayer().toString() + ", do your move: ");
					System.out.println("From: ");
					actionStringFrom = in.readLine();
					System.out.println("To: ");
					actionStringTo = in.readLine();
					action = new Action(actionStringFrom, actionStringTo, client.getPlayer());
					client.write(action);
				}
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK))
				{
					System.out.println("Waiting for your opponent move... ");
				}
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.WHITEWIN))
				{
					System.out.println("YOU WIN!");
					System.exit(0);
				}
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.BLACKWIN))
				{
					System.out.println("YOU LOSE!");
					System.exit(0);
				}
			}
		} 
		else 
		{
			client = new TablutHumanClient("BLACK");
			System.out.println("You are player " + client.getPlayer().toString() + "!");
			while (true) 
			{
				client.read();
				System.out.println("Current state:");
				System.out.println(client.getCurrentState().toString());
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK))
				{
					System.out.println("Player " + client.getPlayer().toString() + ", do your move: ");
					System.out.println("From: ");
					actionStringFrom = in.readLine();
					System.out.println("To: ");
					actionStringTo = in.readLine();
					action = new Action(actionStringFrom, actionStringTo, client.getPlayer());
					client.write(action);
				}
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE))
				{
					System.out.println("Waiting for your opponent move... ");
				}
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.WHITEWIN))
				{
					System.out.println("YOU LOSE!");
					System.exit(0);
				}
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.BLACKWIN))
				{
					System.out.println("YOU WIN!");
					System.exit(0);
				}
			}
			
		}
		
		
		

	}
	
	
}
