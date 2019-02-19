package Tester;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JRadioButton;
import javax.swing.JTextField;

import Domain.Action;
import Domain.Game;
import Domain.State;
import Domain.State.Turn;
import GUI.Gui;

public class CheckerMove implements ActionListener {

	private Gui theGui;
	private JTextField posizione;
	private State state;
	private TestGuiFrame ret;
	private Game game;
	private JRadioButton turno;
	
	public CheckerMove(Gui theGui, JTextField field, State state, TestGuiFrame ret, Game game, JRadioButton jr) {
		super();
		this.setTheGui(theGui);
		this.posizione = field;
		this.state = state;
		this.ret = ret;
		this.game=game;
		this.turno = jr;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Turn t;
		if(turno.isSelected())
		{
			this.state.setTurn(Turn.BLACK);
			t=Turn.BLACK;
		}
		else
		{
			this.state.setTurn(Turn.WHITE);
			t=Turn.WHITE;
		}
		String da = ""+posizione.getText().charAt(0)+posizione.getText().charAt(1);
		String a = ""+posizione.getText().charAt(3)+posizione.getText().charAt(4);
		posizione.setText("");
		Action az = null;
		try {
			az= new Action(da, a, t);
		}
		catch (Exception ex){}
		
		try {
			state=this.game.checkMove(state, az);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println("Mossa non consentita");
		} 
		
		this.ret.setState(state);
		this.theGui.update(state);

	}

	public Gui getTheGui() {
		return theGui;
	}

	public void setTheGui(Gui theGui) {
		this.theGui = theGui;
	}

}
