package it.unibo.ai.didattica.competition.tablut.gui.client;

import it.unibo.ai.didattica.competition.tablut.domain.State;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * Class for the Dialog Window that pops up on application start, to enter Game Informations.
 * 
 * @author Michele Righi
 * (<a href="https://github.com/mikyll">GitHub</a>,
 * <a href="https://www.linkedin.com/in/michele-righi/">LinkedIn</a>)
 */
public class GameInfoDialog extends Dialog<GameInfo>{
	
	public GameInfoDialog(String title, String headerText) {
		this(title, headerText, GameInfo.DEFAULT_SIDE, "", GameInfo.DEFAULT_TIMEOUT, "");
	}
	public GameInfoDialog(String title, String headerText, String side) {
		this(title, headerText, StringUtils.parseSide(side), "", GameInfo.DEFAULT_TIMEOUT, "");
	}
	public GameInfoDialog(String title, String headerText, String side, String username) {
		this(title, headerText, StringUtils.parseSide(side), username, GameInfo.DEFAULT_TIMEOUT, "");
	}
	public GameInfoDialog(String title, String headerText, String side, String username, String timeout) {
		this(title, headerText, StringUtils.parseSide(side), username, StringUtils.parseInteger(timeout), "");
	}
	public GameInfoDialog(String title, String headerText, String side, String username, String timeout, String serverAddress) {
		this(title, headerText, StringUtils.parseSide(side), username, StringUtils.parseInteger(timeout), serverAddress);
	}
	public GameInfoDialog(String title, String headerText, State.Turn side, String username, int timeout, String serverAddress) {
		super();
		super.setTitle(title);
		super.setHeaderText(headerText);
		
		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 50, 10, 10));
		
		// Input elements
		ComboBox<String> comboBoxSide = new ComboBox<String>(
				FXCollections.observableArrayList("Black", "White"));
		comboBoxSide.setValue(StringUtils.capitalize(side.name()));
		comboBoxSide.setPrefWidth(200.0);
		TextField textFieldUsername = new TextField(username);
		textFieldUsername.setPromptText(GameInfo.DEFAULT_USERNAME + StringUtils.capitalize(GameInfo.DEFAULT_SIDE.name()));
		Spinner<Integer> spinnerTimeout = new Spinner<Integer>(GameInfo.MIN_TIMEOUT, GameInfo.MAX_TIMEOUT, timeout, GameInfo.TIMEOUT_INCREMENT);
		spinnerTimeout.setPrefWidth(200.0);
		TextField textFieldAddress = new TextField(serverAddress);
		textFieldAddress.setPromptText(GameInfo.DEFAULT_SERVER_IP);
		
		// Error elements
		Label labelErrorUsername = new Label("Invalid username");
		labelErrorUsername.setFont(Font.font("System", 16));
		labelErrorUsername.setTextFill(Paint.valueOf("red"));
		labelErrorUsername.setTooltip(new Tooltip("Username must be alphanumeric, not shorter than\n3 characters and not longer than 20."));
		labelErrorUsername.setVisible(false);
		Label labelErrorAddress = new Label("Invalid IP address");
		labelErrorAddress.setFont(Font.font("System", 16));
		labelErrorAddress.setTextFill(Paint.valueOf("red"));
		labelErrorAddress.setTooltip(new Tooltip("Server Address must be a valid IPv4 address (X.X.X.X)."));
		labelErrorAddress.setVisible(false);
		
		grid.add(new Label("Side:"), 0, 0);
		grid.add(comboBoxSide, 1, 0);
		grid.add(new Label("Player name:"), 0, 1);
		grid.add(textFieldUsername, 1, 1);
		grid.add(labelErrorUsername, 2, 1);
		grid.add(new Label("Timeout:"), 0, 2);
		grid.add(spinnerTimeout, 1, 2);
		grid.add(new Label("Server address:"), 0, 3);
		grid.add(textFieldAddress, 1, 3);
		grid.add(labelErrorAddress, 2, 3);
		
		super.getDialogPane().setContent(grid);
		
		// Set the button types.
		super.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		// Do some validation (using the Java 8 lambda syntax).
		Node confirmButton = super.getDialogPane().lookupButton(ButtonType.OK);
		comboBoxSide.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if(!oldValue.equals(newValue)) {
				textFieldUsername.setPromptText(GameInfo.DEFAULT_USERNAME + newValue);
			}
		});
		textFieldUsername.textProperty().addListener((observable, oldValue, newValue) -> {
			if(GameInfo.validateUsername(newValue)) {
				textFieldUsername.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
				labelErrorUsername.setVisible(false);
			}
			else {
				textFieldUsername.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
				labelErrorUsername.setVisible(true);
			}
			
			confirmButton.setDisable(
					(!(newValue.trim().isEmpty() || GameInfo.validateUsername(newValue)) ||
					!(textFieldAddress.getText().isEmpty() || GameInfo.validateServerAddress(textFieldAddress.getText()))));
		});
		textFieldAddress.textProperty().addListener((observable, oldValue, newValue) -> {
			if(GameInfo.validateServerAddress(newValue)) {
				textFieldAddress.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
				labelErrorAddress.setVisible(false);
			}
			else {
				textFieldAddress.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
				labelErrorAddress.setVisible(true);
			}
			
			confirmButton.setDisable(
					(!(textFieldUsername.getText().trim().isEmpty() || GameInfo.validateUsername(textFieldUsername.getText())) ||
					!(newValue.isEmpty() || GameInfo.validateServerAddress(newValue))));
		});
		
		// Request focus on the username field by default.
		Platform.runLater(() -> textFieldUsername.requestFocus());

		// Convert the result to a GameInfo object when the login button is clicked.
		super.setResultConverter(dialogButton -> {
		    if (dialogButton == ButtonType.OK) {
		    	String resUsername = textFieldUsername.getText().trim();
		    	if(resUsername.isEmpty()) {
		    		resUsername = textFieldUsername.getPromptText();
		    	}
		    	String resServerIP = textFieldAddress.getText().trim();
		    	if(resServerIP.isEmpty()) {
		    		resServerIP = textFieldAddress.getPromptText();
		    	}
		        return new GameInfo(comboBoxSide.getValue(), resUsername, spinnerTimeout.getValue(), resServerIP);
		    }
		    return null;
		});
	}
	

}