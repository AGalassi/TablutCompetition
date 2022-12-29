package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Optional;

import it.unibo.ai.didattica.competition.tablut.gui.Gui;
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
	private Controller controller;
	
	@Override
    public void start(Stage primaryStage) {
		GameInfoDialog gameInfoDialog = new GameInfoDialog("Game Info Dialog", "Please enter game details");
		Optional<GameInfo> gameInfo = gameInfoDialog.showAndWait();
		
		if(!gameInfo.isPresent()) {
			System.exit(0);
		}
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(Gui.class.getResource("resources/viewHumanGuiClient.fxml"));
		    controller = new Controller(gameInfo.get());
		    fxmlLoader.setController(controller);
		    Scene scene = new Scene(fxmlLoader.load(), 464, 500);
		    scene.getStylesheets().add(Gui.class.getResource("resources/style/style.css").toString());
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

    public static void main(String[] args) {
    	// parse arguments
    	//if ()
    	
        launch(); // run the JavaFX thread
    }
}
