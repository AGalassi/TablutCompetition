package it.unibo.ai.didattica.competition.tablut.gui.client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Auxiliary class with methods to build the board coordinates.
 * 
 * @author Michele Righi
 * (<a href="https://github.com/mikyll">GitHub</a>,
 * <a href="https://www.linkedin.com/in/michele-righi/">LinkedIn</a>)
 */
public class BoardCoordinates {
	public static VBox createVerticalCoordinates(int cellCount, double cellSize, double coordSize) {
		VBox vboxCoordinates = new VBox();
		vboxCoordinates.setPrefHeight(cellSize);
		HBox.setMargin(vboxCoordinates, new Insets(0.0, 1.0, 0.0, 1.0));
		vboxCoordinates.setAlignment(Pos.TOP_CENTER);
		
		for(int i = 0; i < cellCount; i++) {
			Label lRow = new Label("" + (i+1));
			lRow.setFont(Font.font("System", FontWeight.BOLD, 20));
			lRow.setPrefWidth(coordSize);
			lRow.setPrefHeight(cellSize);
			lRow.setMinWidth(Control.USE_PREF_SIZE);
			lRow.setMinHeight(Control.USE_PREF_SIZE);
			lRow.setAlignment(Pos.CENTER);
			
			vboxCoordinates.getChildren().add(lRow);
		}
		
		return vboxCoordinates;
	}
	
	public static HBox createHorizontalCoordinates(int cellCount, double cellSize, double coordSize) {
		HBox hboxCoordinates = new HBox();
		hboxCoordinates.setPrefWidth(cellSize);
		HBox.setMargin(hboxCoordinates, new Insets(1.0, 0.0, 1.0, 0.0));
		hboxCoordinates.setAlignment(Pos.CENTER_LEFT);
		
		for(int i = 0; i < cellCount; i++) {
			Label lCol = new Label("" + ((char)(i+65)));
			lCol.setFont(Font.font("System", FontWeight.BOLD, 20));
			lCol.setPrefWidth(cellSize);
			lCol.setPrefHeight(coordSize);
			lCol.setMinWidth(Control.USE_PREF_SIZE);
			lCol.setMinHeight(Control.USE_PREF_SIZE);
			lCol.setAlignment(Pos.CENTER);
			
			hboxCoordinates.getChildren().add(lCol);
		}
		
		return hboxCoordinates;
	}
}
