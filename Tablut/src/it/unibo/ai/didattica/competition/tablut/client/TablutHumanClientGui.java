package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Optional;

import it.unibo.ai.didattica.competition.tablut.gui.client.Controller;
import it.unibo.ai.didattica.competition.tablut.gui.client.GameInfo;
import it.unibo.ai.didattica.competition.tablut.gui.client.GameInfoDialog;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Class for the Ashton Tablut Client with Graphical User Interface.
 * 
 * @author Michele Righi
 * (<a href="https://github.com/mikyll">GitHub</a>,
 * <a href="https://www.linkedin.com/in/michele-righi/">LinkedIn</a>)
 */
public class TablutHumanClientGui extends Application {
	private static String[] args;
	private Controller controller;
	
	@Override
    public void start(Stage primaryStage) {
		boolean invalidArg = false; // Flag that signals if one or more args are invalid
		String parameters[] = new String[4];
		Optional<GameInfo> gameInfo;
		
		for(int i = 0; i < 4; i++) {
			parameters[i] = "";
		}
		
		if(args.length >= 1) {
			if(GameInfo.validateSide(args[0]))
				parameters[0] = args[0];
			else invalidArg = true;
		}
		if(!invalidArg && args.length >= 2) {
			if(GameInfo.validateUsername(args[1]))
				parameters[1] = args[1];
			else invalidArg = true;
		}
		if(!invalidArg && args.length >= 3) {
			if(GameInfo.validateTimeout(args[2]))
				parameters[2] = args[2];
			else invalidArg = true;
		}
		if(!invalidArg && args.length >= 4) {
			if(GameInfo.validateServerAddress(args[3])) {
				parameters[3] = args[3];
			}
			else invalidArg = true;
		}
		
		if(!invalidArg && args.length == 4) {
			gameInfo = Optional.of(new GameInfo(parameters[0], parameters[1], parameters[2], parameters[3]));
		} else {
			GameInfoDialog gameInfoDialog = new GameInfoDialog("Game Info Dialog", "Please enter game details",
					parameters[0], parameters[1], parameters[2], parameters[3]);
    		gameInfo = gameInfoDialog.showAndWait();
    		
		}
		
		if(!gameInfo.isPresent()) {
			System.exit(0);
		}
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(Controller.RESOURCE_PATH + "views/viewHumanGuiClient.fxml"));
		    controller = new Controller(gameInfo.get());
		    fxmlLoader.setController(controller);
		    Scene scene = new Scene(fxmlLoader.load(), 464, 500);
		    scene.getStylesheets().add(this.getClass().getResource(Controller.RESOURCE_PATH + "styles/style.css").toString());
		    primaryStage.setTitle("Tablut");
		    primaryStage.setResizable(false);
		    primaryStage.setScene(scene);
		    
		    primaryStage.show();
		} catch (IOException e) {
			if(e instanceof ConnectException) {
				Controller.alert(AlertType.ERROR, "Error Dialog", "Connection Error", e.getMessage());
			}
			e.printStackTrace();
		}
	}	
	
	@Override
    public void stop(){
		if(controller != null) {
			// Stop the client thread on exit
			controller.stopClient();
		}
    }

    public static void main(String[] arguments) {
    	args = arguments;
    	
        launch(); // Run the JavaFX thread
    }
}
