package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

/**
 * Tablut random players (tablut game)
 *
 */
public class TablutRandomClient extends TablutClient{
	
	private ClientController controller;

	public TablutRandomClient(String player, int gameChosen) throws UnknownHostException, IOException {
		super(player);
		this.controller = new ClientControllerTablut();
	}
	
	public ClientController getController() {
		return controller;
	}

	public void setController(ClientController controller) {
		this.controller = controller;
	}

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException
	{

		if (args.length == 0) {
			System.out.println("You must specify which player you are (WHITE or BLACK)!");
			System.exit(-1);
		}
		System.out.println("Selected client: " + args[0]);

		TablutRandomClient client = null;
		List<int[]> pawns = new ArrayList<int[]>();
		
		if ((args[0]=="WHITE")||(args[0]=="white"))
		{
			//da modificare
			client = new TablutRandomClient("WHITE", 0);
			
			System.out.println("You are player " + client.getPlayer().toString() + "!");
			
			while (true) 
			{
				client.read();
				System.out.println("Current state:");
				System.out.println(client.getCurrentState().toString());
				try 
				{
					Thread.sleep(1000);
				} 
				catch (InterruptedException e) {}
				//è il mio turno
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE))
				{
					int[] buf;
					for(int i=0; i<client.getCurrentState().getBoard().length; i++)
					{
						for(int j=0; j<client.getCurrentState().getBoard().length; j++)
						{
							if(client.getCurrentState().getPawn(i, j).equalsPawn(State.Pawn.WHITE.toString())||client.getCurrentState().getPawn(i, j).equalsPawn(State.Pawn.KING.toString()))
							{
								buf = new int[2];
								buf[0]=i;
								buf[1]=j;
								pawns.add(buf);
							}
						}						
					}
					
					int[] selected = null;
					
					boolean found=false;
					while(!found)
					{
						selected=pawns.get(new Random().nextInt(pawns.size()-1));
						found=client.getController().canMove(client.getCurrentState(), selected[0], selected[1]);
					}
					System.out.println("Pedina scelta: "+client.getCurrentState().getBox(selected[0], selected[1]));

										
					String to = client.getController().chooseDestination(client.getCurrentState(), selected[0], selected[1]);
					String from = client.getCurrentState().getBox(selected[0], selected[1]);
					
					
					Action a = new Action(from, to, State.Turn.WHITE);
					
					client.write(a);
					pawns.clear();
					
				}
				//è il turno dell'avversario
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK))
				{
					System.out.println("Waiting for your opponent move... ");
				}
				//ho vinto
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.WHITEWIN))
				{
					System.out.println("YOU WIN!");
					System.exit(0);
				}
				//ho perso
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.BLACKWIN))
				{
					System.out.println("YOU LOSE!");
					System.exit(0);
				}
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.DRAW))
				{
					System.out.println("DRAW!");
					System.exit(0);
				}
			}
		} 
		else 
		{
			//da modificare
			client = new TablutRandomClient("BLACK", 0);
			
			
			System.out.println("You are player " + client.getPlayer().toString() + "!");
			while (true) 
			{
				client.read();
				System.out.println("Current state:");
				System.out.println(client.getCurrentState().toString());
				try 
				{
					Thread.sleep(1000);
				} 
				catch (InterruptedException e) {}
				//è il mio turno
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK))
				{
					
					int[] buf;
					for(int i=0; i<client.getCurrentState().getBoard().length; i++)
					{
						for(int j=0; j<client.getCurrentState().getBoard().length; j++)
						{
							if(client.getCurrentState().getPawn(i, j).equalsPawn(State.Pawn.BLACK.toString()))
							{
								buf = new int[2];
								buf[0]=i;
								buf[1]=j;
								pawns.add(buf);
							}
						}						
					}
					
					int[] selected = null;
					
					boolean found=false;
					while(!found)
					{
						selected=pawns.get(new Random().nextInt(pawns.size()-1));
						found=client.getController().canMove(client.getCurrentState(), selected[0], selected[1]);
					}
					System.out.println("Pedina scelta: "+client.getCurrentState().getBox(selected[0], selected[1]));
										
					String to = client.getController().chooseDestination(client.getCurrentState(), selected[0], selected[1]);
					String from = client.getCurrentState().getBox(selected[0], selected[1]);
					
					
					Action a = new Action(from, to, State.Turn.WHITE);
					
					client.write(a);
					pawns.clear();
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
				if(client.getCurrentState().getTurn().equals(StateTablut.Turn.DRAW))
				{
					System.out.println("DRAW!");
					System.exit(0);
				}
			}
		}
	}
			
}

	

