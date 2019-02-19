package Tester;


/**
 * Software to test a particular configuration
 * @author Andrea Piretti
 *
 */
public class Tester {
	
	private TestGui theTestGui;
	private static int gameChosen;
	
	public Tester(int game)
	{		
		setTheTestGui(new TestGui(game));
	}

	public static void main(String[] args)
	{
		if(args.length == 1) 
		{
			try
			{
				gameChosen = Integer.parseInt(args[0]);
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
			System.out.println("Usage: java Tester <game>");
			System.exit(1);
		}
		
		//LANCIO IL MOTORE PER UN SERVER
		Tester tester = new Tester(gameChosen);
		
		tester.run();
		
	}
	
	public void run() {
		while(true)
		{
			
		}
	}

	public TestGui getTheTestGui() {
		return theTestGui;
	}

	public void setTheTestGui(TestGui theTestGui) {
		this.theTestGui = theTestGui;
	}
	
	
	
}
