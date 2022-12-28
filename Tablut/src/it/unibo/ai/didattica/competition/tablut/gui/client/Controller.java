package it.unibo.ai.didattica.competition.tablut.gui.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.exceptions.ActionException;
import it.unibo.ai.didattica.competition.tablut.exceptions.BoardException;
import it.unibo.ai.didattica.competition.tablut.exceptions.CitadelException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingCitadelException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingException;
import it.unibo.ai.didattica.competition.tablut.exceptions.DiagonalException;
import it.unibo.ai.didattica.competition.tablut.exceptions.OccupitedException;
import it.unibo.ai.didattica.competition.tablut.exceptions.PawnException;
import it.unibo.ai.didattica.competition.tablut.exceptions.StopException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ThroneException;
import it.unibo.ai.didattica.competition.tablut.gui.Gui;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * GUI Controller to handle interaction between UI elements and the business logic.
 * 
 * @author Michele Righi
 * (<a href="https://github.com/mikyll">GitHub</a>,
 * <a href="https://www.linkedin.com/in/michele-righi/">LinkedIn</a>)
 */
public class Controller extends TablutClient {
	private final static double CELL_SIZE = 48.0;
	private final static int CELL_COUNT = 9;
	private final static double BORDER_WIDTH = 1.0;
	private final static double BOARD_SIZE = BORDER_WIDTH * 2 + CELL_SIZE * CELL_COUNT;
	
	private Image imageBoard;
	private Image imageBlackPawn;
	private Image imageWhitePawn;
	private Image imageKingPawn;
	
	// Board
	@FXML private AnchorPane anchorPaneBoard;
	@FXML private ImageView imageViewBoard;
	private VBox vboxRowCoordinates;
	private HBox hboxColCoordinates;
	private Cell[][] cells; // [row][column]
	private Rectangle horizontalHighlight;
	private Rectangle verticalHighlight;
	
	// UI
	@FXML private ListView<String> listViewActionsHistory;
	@FXML private Label labelPlayerName;
	@FXML private Label labelPlayerPawn1;
	@FXML private Label labelPlayerPawn2;
	@FXML private Label labelTurn;
	@FXML private HBox hboxMyTurn;
	@FXML private HBox hboxWaitingOpponent;
	@FXML private Label labelMoveFrom;
	@FXML private Label labelMoveTo;
	@FXML private Label labelServerIP;
	
	private GameInfo gameInfo;
	
	private Coordinate coordsFrom;
	private Coordinate coordsTo;
	private List<Coordinate> allowedDestinations = new ArrayList<Coordinate>();
	
	private Thread threadClient;
	private volatile boolean clientRunning = false;
	private GameAshtonTablut game;
	private int turnCounter = 0;
	
	public Controller(GameInfo gameInfo) throws UnknownHostException, IOException {
		super(gameInfo.getSide().name().toUpperCase(), gameInfo.getUsername(), gameInfo.getTimeout(), gameInfo.getServerIP());
		
		this.gameInfo = gameInfo;
		
		imageBoard = new Image(Gui.class.getResourceAsStream("resources/boardAshton.png"));
		
		double boardSize, pawnSize;
		boardSize = imageBoard.getWidth() - BORDER_WIDTH;
		pawnSize = boardSize / CELL_COUNT;
		
		imageBlackPawn = new Image(Gui.class.getResourceAsStream("resources/black3.png"),
				CELL_SIZE, CELL_SIZE, false, false);
		imageWhitePawn = new Image(Gui.class.getResourceAsStream("resources/White2.png"),
				CELL_SIZE, CELL_SIZE, false, false);
		imageKingPawn = new Image(Gui.class.getResourceAsStream("resources/king.png"),
				CELL_SIZE, CELL_SIZE, false, false);
		
		coordsFrom = new Coordinate();
		coordsTo = new Coordinate();
		
		// create new Game
		game = new GameAshtonTablut(0, -1, "logs", "white_ai", "black_ai");

		// start client in separate thread
		threadClient = new Thread(this);
		threadClient.start();
	}
	
	public void initialize() {
		// init game elements
		// board
		imageViewBoard.setImage(imageBoard);
		vboxRowCoordinates = BoardCoordinates.createVerticalCoordinates(CELL_COUNT, CELL_SIZE, 30.0);
		vboxRowCoordinates.setLayoutX(BOARD_SIZE);
		hboxColCoordinates = BoardCoordinates.createHorizontalCoordinates(CELL_COUNT, CELL_SIZE, 30.0);
		hboxColCoordinates.setLayoutY(BOARD_SIZE);
		anchorPaneBoard.getChildren().add(vboxRowCoordinates);
		anchorPaneBoard.getChildren().add(hboxColCoordinates);

		// init UI
		labelPlayerName.setText(gameInfo.getUsername());
		labelTurn.setText("Turn #" + turnCounter + " (" + State.Turn.WHITE.toString() + ")");
		if(gameInfo.getSide().equals(State.Turn.WHITE)) {
			labelPlayerPawn1.setGraphic(new ImageView(imageWhitePawn));
			labelPlayerPawn2.setGraphic(new ImageView(imageKingPawn));
		} else {
			labelPlayerPawn1.setGraphic(new ImageView(imageBlackPawn));
		}
		labelServerIP.setText(" Server IP: " + gameInfo.getServerIP());
		
		resetMove();
	}
	
	private void resetMove() {
		coordsFrom.reset();
		coordsTo.reset();
		labelMoveFrom.setText("");
		labelMoveTo.setText("");
	}
	
	private void updateBoard(State state) {
		Platform.runLater(() -> {
	    	anchorPaneBoard.getChildren().clear();
			anchorPaneBoard.getChildren().add(imageViewBoard);
			anchorPaneBoard.getChildren().add(vboxRowCoordinates);
			anchorPaneBoard.getChildren().add(hboxColCoordinates);
			
			cells = new Cell[state.getBoard().length][state.getBoard().length];
			
			// rows
			for(int row = 0; row < state.getBoard().length; row++) {
				// columns
				for(int col = 0; col < state.getBoard().length; col++) {
					Cell cell = null;
					if(state.getPawn(row, col).equalsPawn("B")) {
						cell = new Cell(imageBlackPawn, CELL_SIZE, row, col,
								state.getTurn().equals(State.Turn.BLACK) && gameInfo.getSide().equals(State.Turn.BLACK),
								(r, c) -> selectPawn(r, c), () -> deselectPawn());
					}
					else if(state.getPawn(row, col).equalsPawn("W")) {
						cell = new Cell(imageWhitePawn, CELL_SIZE, row, col,
								state.getTurn().equals(State.Turn.WHITE) && gameInfo.getSide().equals(State.Turn.WHITE),
								(r, c) -> selectPawn(r, c), () -> deselectPawn());
					}
					else if(state.getPawn(row, col).equalsPawn("K")) {
						cell = new Cell(imageKingPawn, CELL_SIZE, row, col,
								state.getTurn().equals(State.Turn.WHITE) && gameInfo.getSide().equals(State.Turn.WHITE),
								(r, c) -> selectPawn(r, c), () -> deselectPawn());
					}
					if(cell != null) {
						cell.setLayoutX(BORDER_WIDTH + col * CELL_SIZE);
						cell.setLayoutY(BORDER_WIDTH + row * CELL_SIZE);
						anchorPaneBoard.getChildren().add(cell);
						cells[row][col] = cell;
					}
				}
			}
	    });
	}
	
	private void selectPawn(int row, int col) {
		if(coordsFrom.getRow() != -1 && coordsFrom.getCol() != -1) {
			deselectPawn();
		}
		
		coordsFrom = new Coordinate(row, col);
		char chCol = (char) (col + 65);
		labelMoveFrom.setText("" + chCol + (row+1));
		labelMoveTo.setText("");
		
		highlightAllowedMoves(row, col, Paint.valueOf("lime"));
	}
	
	private void deselectPawn() {
		if(cells[coordsFrom.getRow()][coordsFrom.getCol()] != null) {
			cells[coordsFrom.getRow()][coordsFrom.getCol()].deselect();
		}
		
		resetMove();
		
		allowedDestinations.clear();
		
		if(horizontalHighlight != null && verticalHighlight != null) {
			anchorPaneBoard.getChildren().remove(horizontalHighlight);
			anchorPaneBoard.getChildren().remove(verticalHighlight);
			
			horizontalHighlight = null;
			verticalHighlight = null;
		}
	}
	
	/**
	 * Create 2 shapes that highlight the allowed moves only (the cells where the user can move the selected pawn).
	 * @param row represents the row (number) of the selected cell
	 * @param col represents the column (letter) of the selected cell
	 * 
	 * @comment la catena di AND/OR nell'if fa abbastanza schifo,
	 * se qualcuno dovesse trovare una soluzione migliore e' liberissimo di modificarlo :)
	 */
	private void highlightAllowedMoves(int row, int col, Paint color) {
		int yVerRect = row;
		int xHorRect = col;
		int heightVerRect = 1;
		int widthHorRect = 1;
		
		// vertical (same col): top ^
		for(int y = row-1; y >= 0; y--) {
			if (col == 0 && y == 5 ||
					col == 1 && y == 4 ||
					col == 3 && y == 0 ||
					col == 4 && (y == 4 || y == 1) ||
					col == 5 && y == 0 ||
					col == 7 && y == 4 ||
					col == 8 && y == 5)
				break;
			if(super.getCurrentState().getPawn(y, col).equalsPawn("O")) {
				heightVerRect++;
				yVerRect--;
				allowedDestinations.add(new Coordinate(y, col));
			}
			else break;
		}
		// vertical (same col): bottom v
		for(int y = row+1; y < super.getCurrentState().getBoard().length; y++) {
			if (col == 0 && y == 3 ||
					col == 1 && y == 4 ||
					col == 3 && y == 8 ||
					col == 4 && (y == 4 || y == 7) ||
					col == 5 && y == 8 ||
					col == 7 && y == 4 ||
					col == 8 && y == 3)
				break;
			if(super.getCurrentState().getPawn(y, col).equalsPawn("O")) {
				heightVerRect++;
				allowedDestinations.add(new Coordinate(y, col));
			}
			else break;
		}
		// horizontal (same row): left <-
		for(int x = col-1; x >= 0; x--) {
			if (row == 0 && x == 5 ||
					row == 1 && x == 4 ||
					row == 3 && x == 0 ||
					row == 4 && (x == 4 || x == 1) ||
					row == 5 && x == 0 ||
					row == 7 && x == 4 ||
					row == 8 && x == 5) 
				break;
			if(super.getCurrentState().getPawn(row, x).equalsPawn("O")) {
				widthHorRect++;
				xHorRect--;
				allowedDestinations.add(new Coordinate(row, x));
			}
			else break;
		}
		// horizontal (same row): right ->
		for(int x = col+1; x < super.getCurrentState().getBoard().length; x++) {
			if (row == 0 && x == 3 ||
					row == 1 && x == 4 ||
					row == 3 && x == 8 ||
					row == 4 && (x == 4 || x == 7) ||
					row == 5 && x == 8 ||
					row == 7 && x == 4 ||
					row == 8 && x == 3)
				break;
			if(super.getCurrentState().getPawn(row, x).equalsPawn("O")) {
				widthHorRect++;
				allowedDestinations.add(new Coordinate(row, x));
			}
			else break;
		}
		
		verticalHighlight = new Rectangle();
		verticalHighlight.setFill(color);
		verticalHighlight.setWidth(CELL_SIZE);
		verticalHighlight.setHeight(CELL_SIZE * heightVerRect);
		verticalHighlight.setLayoutX(BORDER_WIDTH + col * CELL_SIZE);
		verticalHighlight.setLayoutY(BORDER_WIDTH + yVerRect * CELL_SIZE);
		verticalHighlight.setMouseTransparent(true);
		verticalHighlight.setOpacity(0.5);
		
		horizontalHighlight = new Rectangle();
		horizontalHighlight.setFill(color);
		horizontalHighlight.setWidth(CELL_SIZE * widthHorRect);
		horizontalHighlight.setHeight(CELL_SIZE);
		horizontalHighlight.setLayoutX(BORDER_WIDTH + xHorRect * CELL_SIZE);
		horizontalHighlight.setLayoutY(BORDER_WIDTH + row * CELL_SIZE);
		horizontalHighlight.setMouseTransparent(true);
		horizontalHighlight.setOpacity(0.5);
		
		anchorPaneBoard.getChildren().add(1, verticalHighlight);
		anchorPaneBoard.getChildren().add(1, horizontalHighlight);
	}
	
	private boolean isDestinationAllowed(int row, int col) {
		return allowedDestinations.contains(new Coordinate(row, col));
	}
	
	/**
	 * Perform an action
	 */
	private void doAction() {
		try {
			Action a = new Action(getCoordsString(coordsFrom), getCoordsString(coordsTo), super.getCurrentState().getTurn());
			
			// check if move is possible and get the updated state
			super.setCurrentState(game.checkMove(super.getCurrentState(), a));
			
			// update board with new state
			updateBoard(super.getCurrentState());
			
			// send action to the server
			this.write(a);
			
			addToHistory(a);
			
    		if(!super.getCurrentState().getTurn().equals(State.Turn.WHITEWIN) &&
    				!super.getCurrentState().getTurn().equals(State.Turn.BLACKWIN)) {
    			nextTurn();
    		}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			System.exit(3);
		} catch (BoardException | ActionException | StopException | PawnException | DiagonalException |
				ClimbingException | ThroneException | OccupitedException | ClimbingCitadelException | CitadelException e) {
			e.printStackTrace();
			alert(AlertType.ERROR, "Move Exception", "Illegal Move Exception", e.getMessage());
			System.exit(3);
		}
	}
	
	private void addToHistory(Action a) {
		Platform.runLater(() -> {
			String entry = "";
		
			if(super.getCurrentState().getTurn().equals(gameInfo.getSide())) {
				entry += gameInfo.getUsername();
			}
			else {
				entry += "Player" + gameInfo.getOpponentSideString();
			}
			entry += " moved " + a.getFrom() + " to " + a.getTo();
			
			listViewActionsHistory.getItems().add(entry);
		});
	}
	
	private void nextTurn() {
		if(super.getCurrentState().getTurn().equals(State.Turn.WHITEWIN) ||
				super.getCurrentState().getTurn().equals(State.Turn.BLACKWIN)) {
			stopClient();
		}
		
		Platform.runLater(() -> {
			// disable if not player's turn
			//anchorPaneBoard.setDisable(!super.getCurrentState().getTurn().equals(gameInfo.getSide()));
			
			if(super.getCurrentState().getTurn().equals(State.Turn.WHITEWIN) ||
					super.getCurrentState().getTurn().equals(State.Turn.BLACKWIN)) {
				if((super.getCurrentState().getTurn().equals(State.Turn.WHITEWIN) &&
						gameInfo.getSide().equals(State.Turn.WHITE)) ||
						(super.getCurrentState().getTurn().equals(State.Turn.BLACKWIN) &&
								gameInfo.getSide().equals(State.Turn.BLACK))) {
					alert(AlertType.INFORMATION, "Game Over", "You won!",
							"Winner: " + gameInfo.getUsername() + " (" + gameInfo.getSideString() + ")" + "\nTurns: " + turnCounter);
				}
				else {
					alert(AlertType.INFORMATION, "Game Over", "You lost!",
							"Winner: Player" + gameInfo.getOpponentSideString() + "\nTurns: " + turnCounter);
				}
				System.exit(0);
			}
			
			// update UI
			hboxMyTurn.setVisible(super.getCurrentState().getTurn().equals(gameInfo.getSide()));
			hboxWaitingOpponent.setVisible(!super.getCurrentState().getTurn().equals(gameInfo.getSide()));
			
			// increment turn
			labelTurn.setText("Turn #" + (++turnCounter) + " (" + super.getCurrentState().getTurn().toString() + ")");
		});
	}
	
	@FXML
	private void handleBoardClick(MouseEvent event) {
		int row = (int) event.getY() / 48;
		int col = (int) event.getX() / 48;
		
		if(isDestinationAllowed(row, col)) {
			labelMoveTo.setText(getCoordsString(row, col).toUpperCase());
			coordsTo = new Coordinate(row, col);
			doAction();
			deselectPawn();
		}
		else {
			labelMoveTo.setText("");
		}
	}
	
	
	private String getCoordsString(int row, int col) {
		return getCoordsString(new Coordinate(row, col));
	}
	private String getCoordsString(Coordinate coordinate) {
		char col = (char) (coordinate.getCol() + 97);
		int row = coordinate.getRow() + 1;
		
		return "" + col + row;
	}
	
	private Action obtainAction(State oldState, State newState) throws IOException {
		String from = "", to = "";
		for(int row = 0; row < oldState.getBoard().length; row++) {
			for(int col = 0; col < oldState.getBoard().length; col++) {
				if(oldState.getBoard()[row][col].equals(State.Pawn.EMPTY) &&
						!newState.getBoard()[row][col].equals(State.Pawn.EMPTY)) {
					char chCol = (char) (row + 97);
					to = "" + chCol + row + 1;
					
					// get turn
				}
			}
		}
		return new Action(from, to, oldState.getTurn());
	}
	
	public static void alert(AlertType type, String title, String headerMessage, String contentMessage) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(headerMessage);
		alert.setContentText(contentMessage);
		alert.showAndWait();
	}

	@Override
	public void run() {
		clientRunning = true;
		
		// Send name to the server
        try {
            super.declareName();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        try {
        	while(clientRunning) {
        		State oldState = this.getCurrentState();
        		
        		// read new state from the server
        		this.read();
        		
        		State newState = this.getCurrentState();
        		// Action opponentAction = make a method to calculate the action comparing 2 states
        		
        		// update
        		updateBoard(newState);
        		
        		nextTurn();
        		
        		// add to history
        		//addToHistory(obtainAction(oldState, newState));
        		
        		try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {}
        	}
        } catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			System.exit(2);
		}
	}
	
	public void stopClient() {
		clientRunning = false;
	}
}
