package it.unibo.ai.didattica.competition.tablut.gui.client;

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
		super();
		super.setTitle(title);
		super.setHeaderText(headerText);
		
		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 50, 10, 10));
		
		// Input elements
		TextField textFieldUsername = new TextField();
		textFieldUsername.setPromptText(GameInfo.DEFAULT_USERNAME + capitalize(GameInfo.DEFAULT_SIDE.name()));
		ComboBox<String> comboBoxSide = new ComboBox<String>(
				FXCollections.observableArrayList("Black", "White"));
		comboBoxSide.setValue(capitalize(GameInfo.DEFAULT_SIDE.name()));
		comboBoxSide.setPrefWidth(200.0);
		Spinner<Integer> spinnerTimeout = new Spinner<Integer>(GameInfo.MIN_TIMEOUT, GameInfo.MAX_TIMEOUT, GameInfo.DEFAULT_TIMEOUT, GameInfo.TIMEOUT_INCREMENT);
		spinnerTimeout.setPrefWidth(200.0);
		TextField textFieldAddress = new TextField();
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
		
		grid.add(new Label("Player name:"), 0, 0);
		grid.add(textFieldUsername, 1, 0);
		grid.add(labelErrorUsername, 2, 0);
		grid.add(new Label("Side:"), 0, 1);
		grid.add(comboBoxSide, 1, 1);
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
		textFieldUsername.textProperty().addListener((observable, oldValue, newValue) -> {
			if(validateUsername(newValue)) {
				textFieldUsername.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
				labelErrorUsername.setVisible(false);
			}
			else {
				textFieldUsername.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
				labelErrorUsername.setVisible(true);
			}
			
			confirmButton.setDisable(
					(!validateUsername(newValue)) || !validateServerAddress(textFieldAddress.getText()));
		});
		comboBoxSide.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if(!oldValue.equals(newValue)) {
				textFieldUsername.setPromptText(GameInfo.DEFAULT_USERNAME + newValue);
			}
		});
		textFieldAddress.textProperty().addListener((observable, oldValue, newValue) -> {
			if(validateServerAddress(newValue)) {
				textFieldAddress.setStyle("-fx-border-width: 0px; -fx-focus-color: #039ED3;");
				labelErrorAddress.setVisible(false);
			}
			else {
				textFieldAddress.setStyle("-fx-text-box-border: red; -fx-focus-color: red;");
				labelErrorAddress.setVisible(true);
			}
			
			confirmButton.setDisable(
					(!validateUsername(newValue)) || !validateServerAddress(textFieldAddress.getText()));
		});
		
		// Request focus on the username field by default.
		Platform.runLater(() -> textFieldUsername.requestFocus());

		// Convert the result to a GameInfo object when the login button is clicked.
		super.setResultConverter(dialogButton -> {
		    if (dialogButton == ButtonType.OK) {
		    	String username = textFieldUsername.getText().trim();
		    	if(username.isEmpty()) {
		    		username = textFieldUsername.getPromptText();
		    	}
		    	String serverIP = textFieldAddress.getText().trim();
		    	if(serverIP.isEmpty()) {
		    		serverIP = textFieldAddress.getPromptText();
		    	}
		        return new GameInfo(username, comboBoxSide.getValue(), spinnerTimeout.getValue(), serverIP);
		    }
		    return null;
		});
	}
	
	private boolean validateUsername(String username) {
		return username.trim().isEmpty() || GameInfo.PATTERN_USERNAME.matcher(username.trim()).matches();
	}
	private boolean validateServerAddress(String address) {
		return address.isEmpty() || GameInfo.PATTERN_IP.matcher(address).matches();
	}
	
    private String capitalize(String s) {
    	return s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }
}