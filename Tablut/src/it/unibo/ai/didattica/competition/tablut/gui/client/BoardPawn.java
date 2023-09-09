package it.unibo.ai.didattica.competition.tablut.gui.client;

import java.util.function.BiConsumer;

import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * Pawn of the GUI board for the Ashton Tablut Client with Graphical User Interface.
 * 
 * @author Michele Righi
 * (<a href="https://github.com/mikyll">GitHub</a>,
 * <a href="https://www.linkedin.com/in/michele-righi/">LinkedIn</a>)
 */
public class BoardPawn extends ImageView {
	private final static double OFFSET = 2.0;
	
	private BiConsumer<Integer, Integer> selectCallback;
	private Runnable deselectCallback;
	
	private boolean selected = false;
	
	private double size;
	private int row, col;
	
	private DropShadow effectGlow;
	private DropShadow effectShadow;
	
	public static BoardPawn createDestinationBoardPawn(Image image, double size, int row, int col) {
		BoardPawn res = new BoardPawn(image, size, row, col);
		res.setLayoutX(col * size - OFFSET);
		res.setLayoutY(row * size - OFFSET);
		
		DropShadow effectShadow = new DropShadow();
		effectShadow.setWidth(30.0);
		effectShadow.setHeight(30.0);
		effectShadow.setRadius(15.0);
		effectShadow.setSpread(0.4);
		effectShadow.setOffsetX(OFFSET * 2);
		effectShadow.setOffsetY(OFFSET * 2);
		res.setEffect(effectShadow);
		
		return res;
	}
	
	public BoardPawn(Image image, double size, int row, int col) {
		this(image, size, row, col, false, null, null);
	}
	public BoardPawn(Image image, double size, int row, int col,
			boolean selectable,
			BiConsumer<Integer, Integer> select,
			Runnable deselect) {
		super(image);
		super.setFitWidth(size);
		super.setFitHeight(size);
		super.setPreserveRatio(true);
		
		this.size = size;
		this.row = row;
		this.col = col;
		
		this.selectCallback = select;
		this.deselectCallback = deselect;
		
		effectGlow = new DropShadow();
		effectGlow.setWidth(30.0);
		effectGlow.setHeight(30.0);
		effectGlow.setRadius(15.0);
		effectGlow.setSpread(0.4);
		effectGlow.setColor(Color.RED);
		
		effectShadow = new DropShadow();
		effectShadow.setWidth(30.0);
		effectShadow.setHeight(30.0);
		effectShadow.setRadius(15.0);
		effectShadow.setSpread(0.4);
		effectShadow.setOffsetX(OFFSET * 2);
		effectShadow.setOffsetY(OFFSET * 2);

		if(selectable) {
			super.setOnMouseClicked(this::toggle);
			super.setOnMouseEntered(mouseEvent -> {
				super.setCursor(Cursor.HAND);
				
				if(!selected) {
					super.setEffect(effectGlow);
				}
			});
			super.setOnMouseExited(mouseEvent -> {
				super.setCursor(Cursor.DEFAULT);
				if(!selected) {
					super.setEffect(null);
				}
			});
		}
		//super.disableProperty().addListener((observable, oldValue, newValue) -> {System.out.println("changed: " + oldValue + ", " + newValue); super.setOpacity(newValue == true ? 0.8 : 1);});
	}
	
	public void toggle(MouseEvent event) {
		if(selected) {
			deselect();
			deselectCallback.run();
		}
		else {
			select();
			selectCallback.accept(row, col);
		}
	}
	public void select() {
		selected = true;
		
		super.setLayoutX(col * size - OFFSET);
		super.setLayoutY(row * size - OFFSET);
		
		super.setEffect(effectShadow);
		
		super.setOpacity(0.5);
	}
	public void deselect() {
		selected = false;
		
		super.setLayoutX(col * size + 1);
		super.setLayoutY(row * size + 1);
		
		super.setEffect(null);
		
		super.setOpacity(1.0);
	}
	public boolean isSelected() {
		return selected;
	}
	
	public void setBoardPosition(Coordinate coord) {
		setBoardPosition(coord.getRow(), coord.getCol());
	}
	public void setBoardPosition(int row, int col) {
		super.setLayoutX(col * size - OFFSET);
		super.setLayoutY(row * size - OFFSET);
	}
}
