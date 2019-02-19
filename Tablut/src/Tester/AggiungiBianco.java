package Tester;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JTextField;

import Domain.Action;
import Domain.State;
import Domain.State.Pawn;
import Domain.State.Turn;
import GUI.Gui;

public class AggiungiBianco implements ActionListener {

	private Gui theGui;
	private JTextField posizione;
	private State state;
	private TestGuiFrame ret;
	
	
	
	public AggiungiBianco(Gui theGui, JTextField field, State state, TestGuiFrame ret) {
		super();
		this.theGui = theGui;
		this.posizione = field;
		this.state = state;
		this.ret = ret;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String casella = posizione.getText();
		posizione.setText("");
		Action a = null; 
		try {
			a = new Action(casella, casella, Turn.WHITE);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int column = a.getColumnFrom();
		int row = a.getRowFrom();
		this.state.getBoard()[row][column]=Pawn.WHITE;
		this.theGui.update(state);
		this.ret.setState(state);
	}

}
