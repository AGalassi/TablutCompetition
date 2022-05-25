package it.unibo.ai.didattica.competition.tablut.gui;

import javax.swing.JFrame;

import it.unibo.ai.didattica.competition.tablut.domain.State;

/**
 * 
 * This class represent an instrument that control the graphics
 * @author A.Piretti
 *
 */
public class Gui {
	
	Background background;
	JFrame frame;
	private int game;
	
	public Gui(int game) {
		super();
		this.game = game;
		initGUI();
		show();
	}
	
	
	/**
	 * Update the graphic whit a new state of the game
	 * @param aState represent the new state of the game
	 */
	public void update(State aState) {
		background.setaState(aState);
		frame.repaint();
	}	
	
	/**
	 * Initialization
	 */
	private void initGUI() {
		switch (this.game) {
		case 1:
			background = new BackgroundTablut();
			break;
		case 2:
			background = new BackgroundTablut();
			break;
		case 3:
			background = new BackgroundBrandub();
			break;
		case 4:
			background = new BackgroundTablut();
			break;
		default:
			System.out.println("Error in GUI init");
			System.exit(4);
		}

		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Più performante fare paint su
		// JPanel (cioè background) che
		// direttamente sul frame
		frame.getContentPane().add(background);
		frame.pack();
		frame.setLocationByPlatform(true);
	}
	
	/**
	 * Display the window
	 */
	private void show() {
		switch (game) {
		case 1:
			frame.setSize(415, 415);
			frame.setTitle("ClassicTablut");
			frame.setVisible(true);
			break;
		case 2:
			frame.setSize(415, 415);
			frame.setTitle("ModernTablut");
			frame.setVisible(true);
			break;
		case 3:
			frame.setSize(415, 415);
			frame.setTitle("Brandub");
			frame.setVisible(true);
			break;
		case 4:
			frame.setSize(390, 435);
			frame.setTitle("Tablut");
			frame.setVisible(true);
			break;
		default:
			System.out.println("Error in GUI show");
			System.exit(4);
		}
	}

}
