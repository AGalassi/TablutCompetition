package it.unibo.ai.didattica.competition.tablut.gui.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalTime;

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
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

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
	
	// UI
	@FXML private Label labelServerIP;
	@FXML private Label labelTimer;
	@FXML private Button buttonEnlarge;
	@FXML private ListView<String> listViewActionsHistory;
	@FXML private Label labelPlayerName;
	@FXML private Label labelPlayerPawn1;
	@FXML private Label labelPlayerPawn2;
	@FXML private Label labelTurn;
	@FXML private HBox hboxMyTurn;
	@FXML private HBox hboxWaitingOpponent;
	@FXML private Label labelMoveFrom;
	@FXML private Label labelMoveTo;
	
	private GameInfo gameInfo;
	
	private Coordinate coordsFrom;
	private Coordinate coordsTo;
	
	private AllowedMoves allowedMoves;
	private Shape highlight;
	
	private Thread threadClient;
	private volatile boolean clientRunning = false;
	private GameAshtonTablut game;
	private int turnCounter = 0;
	private LocalTime startTime;
	
	private AnimationTimer timer;
	
	public Controller(GameInfo gameInfo) throws UnknownHostException, IOException {
		super(gameInfo.getSide().name().toUpperCase(), gameInfo.getUsername(), gameInfo.getTimeout(), gameInfo.getServerIP());
		
		this.gameInfo = gameInfo;
		
		// Load images
		imageBoard = new Image(Gui.class.getResourceAsStream("resources/boardAshton.png"));
		imageBlackPawn = new Image(Gui.class.getResourceAsStream("resources/black3.png"),
				CELL_SIZE, CELL_SIZE, false, false);
		imageWhitePawn = new Image(Gui.class.getResourceAsStream("resources/White2.png"),
				CELL_SIZE, CELL_SIZE, false, false);
		imageKingPawn = new Image(Gui.class.getResourceAsStream("resources/king.png"),
				CELL_SIZE, CELL_SIZE, false, false);
		
		coordsFrom = new Coordinate();
		coordsTo = new Coordinate();
		
		// Create new Game
		game = new GameAshtonTablut(0, -1, "logs", "white_ai", "black_ai");

		// Start client in a separate thread
		threadClient = new Thread(this);
		threadClient.start();
		
		/*timer = new AnimationTimer() {
			private long lastFrame = 0;
	        @Override
	        public void handle(long now) {
	            if ((now - lastFrame)*2 > nbSecPerFrame) {
	                GameManager.update();
	                lastFrame = now;
	            }
	        }
		};*/
	}
	
	public void initialize() {
		// Init board
		imageViewBoard.setImage(imageBoard);
		imageViewBoard.setFitWidth(BOARD_SIZE);
		imageViewBoard.setFitHeight(BOARD_SIZE);
		imageViewBoard.setPreserveRatio(true);
		
		vboxRowCoordinates = BoardCoordinates.createVerticalCoordinates(CELL_COUNT, CELL_SIZE, 30.0);
		vboxRowCoordinates.setLayoutX(BOARD_SIZE);
		hboxColCoordinates = BoardCoordinates.createHorizontalCoordinates(CELL_COUNT, CELL_SIZE, 30.0);
		hboxColCoordinates.setLayoutY(BOARD_SIZE);
		anchorPaneBoard.getChildren().add(vboxRowCoordinates);
		anchorPaneBoard.getChildren().add(hboxColCoordinates);

		// init UI
		labelServerIP.setText(" Server IP: " + gameInfo.getServerIP());
		buttonEnlarge.setId("enlarge");
		labelPlayerName.setText(gameInfo.getUsername());
		labelTurn.setText("Turn #" + turnCounter + " (" + State.Turn.WHITE.toString() + ")");
		if(gameInfo.getSide().equals(State.Turn.WHITE)) {
			labelPlayerPawn1.setGraphic(new ImageView(imageWhitePawn));
			labelPlayerPawn2.setGraphic(new ImageView(imageKingPawn));
		} else {
			labelPlayerPawn1.setGraphic(new ImageView(imageBlackPawn));
		}
		
		resetMove();
	}
	
	private void resetMove() {
		coordsFrom.reset();
		coordsTo.reset();
		labelMoveFrom.setText("");
		labelMoveTo.setText("");
	}
	
	@FXML private void toggleEnlarge(ActionEvent event) {
		Stage stage = (Stage) buttonEnlarge.getScene().getWindow();
		
		if(stage.getWidth() < 500) {
			stage.setWidth(978);
			buttonEnlarge.setId("shrink");
		}
		else {
			stage.setWidth(482);
			buttonEnlarge.setId("enlarge");
		}
	}
	
	private void updateTimer() {
		
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
		
		highlightAllowedMoves(row, col);
	}
	
	private void deselectPawn() {
		if(cells[coordsFrom.getRow()][coordsFrom.getCol()] != null) {
			cells[coordsFrom.getRow()][coordsFrom.getCol()].deselect();
		}
		
		resetMove();
		
		// Reset allowed moves
		if(allowedMoves != null && highlight != null) {
			anchorPaneBoard.getChildren().remove(highlight);
			allowedMoves.getAllowedDestinations().clear();
			
			allowedMoves = null;
			highlight = null;
		}
	}

	private void highlightAllowedMoves(int row, int col) {
		allowedMoves = new AllowedMoves(super.getCurrentState(), row, col, CELL_SIZE);
		highlight = allowedMoves.getHighlight(BORDER_WIDTH);
		anchorPaneBoard.getChildren().add(1, highlight);
	}
	
	private boolean isDestinationAllowed(int row, int col) {
		if(allowedMoves == null)
			return false;
		return allowedMoves.getAllowedDestinations().contains(new Coordinate(row, col));
	}
	
	/**
	 * Perform an action
	 */
	private void doAction() {
		try {
			Action a = new Action(getCoordsString(coordsFrom), getCoordsString(coordsTo), super.getCurrentState().getTurn());
			
			// Check if move is possible and get the updated state
			super.setCurrentState(game.checkMove(super.getCurrentState(), a));
			
			// Update board with new state
			updateBoard(super.getCurrentState());
			
			// Send action to the server
			this.write(a);
			
			addToHistory(a);
			
    		if(!super.getCurrentState().getTurn().equals(State.Turn.WHITEWIN) &&
    				!super.getCurrentState().getTurn().equals(State.Turn.BLACKWIN)) {
    			nextTurn(0);
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
			String entry = "Turn #" + turnCounter + ": ";
		
			if(!super.getCurrentState().getTurn().equals(gameInfo.getSide())) {
				entry += gameInfo.getUsername();
			}
			else {
				entry += "Player" + gameInfo.getOpponentSideString();
			}
			entry += " moved from " + a.getFrom().toUpperCase() + " to " + a.getTo().toUpperCase();
			
			listViewActionsHistory.getItems().add(entry);
		});
	}
	
	private void nextTurn(int turnIncrement) {
		// Stop the client if the game is over
		if(super.getCurrentState().getTurn().equals(State.Turn.WHITEWIN) ||
				super.getCurrentState().getTurn().equals(State.Turn.BLACKWIN)) {
			stopClient();
		}
		
		Duration elapsedTime = Duration.between(startTime, LocalTime.now());
		long elapsedSeconds = Math.abs(elapsedTime.getSeconds());
		String elapsedTimeFormatted = String.format(
		        "%02d:%02d:%02d",
		        elapsedSeconds / 3600,
		        (elapsedSeconds % 3600) / 60,
		        elapsedSeconds % 60);
		
		Platform.runLater(() -> {
			// Show alert dialog if the game is over
			if(super.getCurrentState().getTurn().equals(State.Turn.WHITEWIN) ||
					super.getCurrentState().getTurn().equals(State.Turn.BLACKWIN)) {
				if((super.getCurrentState().getTurn().equals(State.Turn.WHITEWIN) &&
						gameInfo.getSide().equals(State.Turn.WHITE)) ||
						(super.getCurrentState().getTurn().equals(State.Turn.BLACKWIN) &&
								gameInfo.getSide().equals(State.Turn.BLACK))) {
					alert(AlertType.INFORMATION, "Game Over", "You won!",
							"Winner: " + gameInfo.getUsername() + " (" + gameInfo.getSideString() + ")" +
							"\nTurns: " + turnCounter +
							"\nTime elapsed: " + elapsedTimeFormatted);
				}
				else {
					alert(AlertType.INFORMATION, "Game Over", "You lost!",
							"Winner: Player" + gameInfo.getOpponentSideString() +
							"\nTurns: " + turnCounter +
							"\nTime elapsed: " + elapsedTimeFormatted);
				}
				System.exit(0);
			}
			
			// Update UI
			hboxMyTurn.setVisible(super.getCurrentState().getTurn().equals(gameInfo.getSide()));
			hboxWaitingOpponent.setVisible(!super.getCurrentState().getTurn().equals(gameInfo.getSide()));
			
			// Increment turn
			turnCounter += turnIncrement;
			labelTurn.setText("Turn #" + turnCounter + " (" + super.getCurrentState().getTurn().toString() + ")");
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
	
	/**
	 * Obtain an action comparing two states
	 * @param oldState the state before the action
	 * @param newState the state after the action
	 * @return the action performed to obtain newState
	 * @throws IOException
	 */
	private Action obtainAction(State oldState, State newState) throws IOException {
		String from = "", to = "";
		for(int row = 0; row < oldState.getBoard().length; row++) {
			for(int col = 0; col < oldState.getBoard().length; col++) {
				if(oldState.getBoard()[row][col].equals(State.Pawn.EMPTY) &&
						!newState.getBoard()[row][col].equals(State.Pawn.EMPTY)) {
					char chCol = (char) (row + 97);
					to = "" + chCol + row + 1;
					
					System.out.println("TO: " + to); // test
					
					// get turn
					// TO-DO: obtain the "to" coordinates
					// check if surrounding contains an eaten pawn ?
					// <-
					if(row > 0) {
						
					}
				}
			}
		}
		return new Action(from, to, oldState.getTurn());
	}
	private void tmpOpponentAddToHistory(State oldState, State newState) {
		if(super.getCurrentState().getTurn().equals(gameInfo.getSide())) {
			Platform.runLater(() -> {
				String to = "Turn #" + turnCounter + ": Player" + gameInfo.getOpponentSideString() + " moved to ";
				for(int row = 0; row < oldState.getBoard().length; row++) {
					for(int col = 0; col < oldState.getBoard().length; col++) {
						if(oldState.getBoard()[row][col].equals(State.Pawn.EMPTY) &&
								!newState.getBoard()[row][col].equals(State.Pawn.EMPTY)) {
							char chCol = (char) (row + 65);
							to += "" + chCol + (row+1);
							
							break;
						}
					}
				}
				listViewActionsHistory.getItems().add(to);
			});
		}
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
        		
        		// Read a new state from the server
        		this.read();
        		if(startTime == null) 
        			startTime = LocalTime.now();
        		
        		State newState = this.getCurrentState();
        		
        		// Update board UI
        		updateBoard(newState);
        		
        		// Add to history
        		// TO-DO: addToHistory(obtainAction(oldState, newState));
        		// Temporary solution (only destination)
        		if(turnCounter != 0 && !(turnCounter == 1 && gameInfo.getSide().equals(State.Turn.WHITE))) {
        			System.out.println("test" + turnCounter + " - " + gameInfo.getSide());
        			tmpOpponentAddToHistory(oldState, newState);
        		}
        		
        		nextTurn(1);
        		
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
