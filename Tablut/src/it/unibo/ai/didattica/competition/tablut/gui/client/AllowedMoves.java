package it.unibo.ai.didattica.competition.tablut.gui.client;

import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * Auxiliary class which provides the allowed destination for a selected pawn.
 * 
 * @author Michele Righi
 * (<a href="https://github.com/mikyll">GitHub</a>,
 * <a href="https://www.linkedin.com/in/michele-righi/">LinkedIn</a>)
 * 
 * @comment Usage:
 * <ol>
 * 	<li>Create an AllowedMoves object (<code>new AllowedMoves(...)</code>) for a given state,
 *	passing the row and columns of the selected pawn;</li>
 * 	<li>Call <code>getHighlight(...)</code> to get the highlight shape and add it to the JavaFX node;</li>
 * 	<li>Call <code>getAllowedDestinations()</code> to get the list of coordinates where the user can move the selected pawn.</li>
 * </ol>
 */
public class AllowedMoves {
	private Rectangle verticalHighlight;
	private Rectangle horizontalHighlight;
	private List<Coordinate> allowedDestinations;
	
	public AllowedMoves(State state, int row, int col) {
		this(state, row, col, 48.0);
	}
	/**
	 * Create 2 rectangles that highlight the allowed moves only (the cells where the user can move the selected pawn),
	 * and a list containing the coordinates of the allowed moves for the selected pawn.
	 * @param state represents the current state of the game
	 * @param row represents the row (number) of the selected cell
	 * @param col represents the column (letter) of the selected cell
	 * @param cellSize is the size of the cell from the selected pawn
	 * 
	 * @comment the AND/OR chain inside the if conditions is horrible and gives eyes cancer,
	 * if anyone could find a better solution feel free to edit it :)
	 */
	public AllowedMoves(State state, int row, int col, double cellSize) {
		allowedDestinations = new ArrayList<Coordinate>();
		
		int yVerRect = row;
		int xHorRect = col;
		int heightVerRect = 1;
		int widthHorRect = 1;
		
		// Vertical (same col): top ^
		for(int y = row-1; y >= 0; y--) {
			if (col == 0 && y == 5 ||
					col == 1 && y == 4 ||
					col == 3 && y == 0 ||
					col == 4 && (y == 4 || y == 1) ||
					col == 5 && y == 0 ||
					col == 7 && y == 4 ||
					col == 8 && y == 5)
				break;
			if(state.getPawn(y, col).equalsPawn("O")) {
				heightVerRect++;
				yVerRect--;
				allowedDestinations.add(new Coordinate(y, col));
			}
			else break;
		}
		// Vertical (same col): bottom v
		for(int y = row+1; y < state.getBoard().length; y++) {
			if (col == 0 && y == 3 ||
					col == 1 && y == 4 ||
					col == 3 && y == 8 ||
					col == 4 && (y == 4 || y == 7) ||
					col == 5 && y == 8 ||
					col == 7 && y == 4 ||
					col == 8 && y == 3)
				break;
			if(state.getPawn(y, col).equalsPawn("O")) {
				heightVerRect++;
				allowedDestinations.add(new Coordinate(y, col));
			}
			else break;
		}
		// Horizontal (same row): left <-
		for(int x = col-1; x >= 0; x--) {
			if (row == 0 && x == 5 ||
					row == 1 && x == 4 ||
					row == 3 && x == 0 ||
					row == 4 && (x == 4 || x == 1) ||
					row == 5 && x == 0 ||
					row == 7 && x == 4 ||
					row == 8 && x == 5) 
				break;
			if(state.getPawn(row, x).equalsPawn("O")) {
				widthHorRect++;
				xHorRect--;
				allowedDestinations.add(new Coordinate(row, x));
			}
			else break;
		}
		// Horizontal (same row): right ->
		for(int x = col+1; x < state.getBoard().length; x++) {
			if (row == 0 && x == 3 ||
					row == 1 && x == 4 ||
					row == 3 && x == 8 ||
					row == 4 && (x == 4 || x == 7) ||
					row == 5 && x == 8 ||
					row == 7 && x == 4 ||
					row == 8 && x == 3)
				break;
			if(state.getPawn(row, x).equalsPawn("O")) {
				widthHorRect++;
				allowedDestinations.add(new Coordinate(row, x));
			}
			else break;
		}
		
		verticalHighlight = new Rectangle();
		verticalHighlight.setWidth(cellSize);
		verticalHighlight.setHeight(cellSize * heightVerRect);
		verticalHighlight.setLayoutX(col * cellSize);
		verticalHighlight.setLayoutY(yVerRect * cellSize);
		
		horizontalHighlight = new Rectangle();
		horizontalHighlight.setWidth(cellSize * widthHorRect);
		horizontalHighlight.setHeight(cellSize);
		horizontalHighlight.setLayoutX(xHorRect * cellSize);
		horizontalHighlight.setLayoutY(row * cellSize);
	}
	
	public Shape getHighlight() {
		return getHighlight(0.0, Paint.valueOf("lime"));
	}
	public Shape getHighlight(double offset) {
		return getHighlight(offset, Paint.valueOf("lime"));
	}
	public Shape getHighlight(Paint color) {
		return getHighlight(0.0, color);
	}
	/**
	 * Creates a shape which combines the 2 highlight rectangles
	 * @param borderOffset X and Y offset (default is 0.0)
	 * @param color is the color fill of the rectangles (default is "lime")
	 * @return a Shape
	 */
	public Shape getHighlight(double borderOffset, Paint color) {
		Shape result = Shape.union(verticalHighlight, horizontalHighlight);
		result.setOpacity(0.5);
		result.setMouseTransparent(true);
		result.setFill(color);
		result.setLayoutX(borderOffset);
		result.setLayoutY(borderOffset);
		return result;
	}
	
	/**
	 * Provide a list containing the coordinates of the allowed destinations for the selected pawn.
	 * @return a list of Coordinate objects
	 */
	public List<Coordinate> getAllowedDestinations() {
		return allowedDestinations;
	}
}
