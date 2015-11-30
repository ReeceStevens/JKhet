package jkhet;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class Gui extends Application {
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("JKhet");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setPadding(new Insets(25,25,25,25));
		grid.setHgap(10);
		grid.setVgap(10);

		int row = 0;
		Label title = new Label("Welcome to JKhet!");
		grid.add(title,0,row);
		row ++;
	
		Label p1 = new Label("Player 1: ");
		grid.add(p1,0,row);
		ObservableList<String> p1_options = FXCollections.observableArrayList(
			"Human",
			"Computer"
		);
		final ComboBox<String> p1Options = new ComboBox<>(p1_options);
		p1Options.setValue("Human");
		grid.add(p1Options,1,row);
		row ++;

		Label p2 = new Label("Player 2: ");
		grid.add(p2,0,row);
		ObservableList<String> p2_options = FXCollections.observableArrayList(
			"Human",
			"Computer"
		);
		final ComboBox<String> p2Options = new ComboBox<>(p2_options);
		p2Options.setValue("Human");
		grid.add(p2Options,1,row);
		row ++;

		Label gm = new Label("Game Mode: ");
		grid.add(gm,0,row);
		ObservableList<String> gm_options = FXCollections.observableArrayList(
			"Classic",
			"Dynasty",
			"Imhotep"
		);
		final ComboBox<String> gameModeOptions = new ComboBox<>(gm_options);
		gameModeOptions.setValue("Classic");
		grid.add(gameModeOptions,1,row);
		row ++;

		final Text feedbackTarget = new Text();
		row ++; 
		grid.add(feedbackTarget,0,row);

		Button startGame = new Button("Start Game");
		grid.add(startGame,1,row);
		startGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override		
			public void handle(ActionEvent event) {
				if (p1Options.getValue().equals("Computer")) {	
					feedbackTarget.setFill(Color.FIREBRICK);
					feedbackTarget.setText("Computer functionality\n not yet implemented.");
				}
				else if (p2Options.getValue().equals("Computer")) {	
					feedbackTarget.setFill(Color.FIREBRICK);
					feedbackTarget.setText("Computer functionality\n not yet implemented.");
				}
				else {
					feedbackTarget.setText("");
					switch(gameModeOptions.getValue()) {
						case "Classic":
							Piece.setupBoard(Piece.SetupType.CLASSIC);
							break;
						case "Dynasty":
							Piece.setupBoard(Piece.SetupType.DYNASTY);
							break;
						case "Imhotep":
							Piece.setupBoard(Piece.SetupType.IMHOTEP);
							break;
					}
					launchGame();
				}
			}
		});

		Scene scene = new Scene(grid, 500,500);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/**
	 * launchGame() -- open game window and begin playing
	 */
	public static void launchGame() {
		int window_height = 500;
		int window_width = 1000;
		Stage game = new Stage();
		GridPane masterGrid = new GridPane();
		GridPane board = new GridPane();
		int min = window_height / Params.BOARD_HEIGHT;
		for (int i = 0; i < Params.BOARD_WIDTH; i += 1) {
			for (int j = 0; j < Params.BOARD_HEIGHT; j += 1) {
				javafx.scene.shape.Rectangle clear_rect = new javafx.scene.shape.Rectangle(0,0,min-1,min-1);
				clear_rect.setFill(Color.DIMGREY);
				clear_rect.setStroke(Color.BLACK);
				board.add(clear_rect,i,j);
			}
		}
		for (Piece a : Piece.board_pieces) {
			// Draw all Pieces.
			String img_url = "";
			if (a instanceof Pyramid) {
				if (a.player == 2) {
					switch(a.rot) {
						case 0:
							img_url = "file:jkhet/imgs/pyramid_p2_rot0.png";
							break;
						case 1:
							img_url = "file:jkhet/imgs/pyramid_p2_rot1.png";
							break;
						case 2:
							img_url = "file:jkhet/imgs/pyramid_p2_rot2.png";
							break;
						case 3:
							img_url = "file:jkhet/imgs/pyramid_p2_rot3.png";
					}
				}
				else if (a.player == 1) {
					switch(a.rot) {
						case 0:
							img_url = "file:jkhet/imgs/pyramid_p1_rot0.png";
							break;
						case 1:
							img_url = "file:jkhet/imgs/pyramid_p1_rot1.png";
							break;
						case 2:
							img_url = "file:jkhet/imgs/pyramid_p1_rot2.png";
							break;
						case 3:
							img_url = "file:jkhet/imgs/pyramid_p1_rot3.png";
					}
				}
			}
			else if (a instanceof Djed) {
				if (a.player == 1) {
					switch(a.rot) {
						case 0:
						case 2:
							img_url = "file:jkhet/imgs/djed_p1_rot0.png";
							break;
						case 1:
						case 3:
							img_url = "file:jkhet/imgs/djed_p1_rot1.png";
							
					}
				}
				else if (a.player == 2) {
					switch(a.rot) {
						case 0:
						case 2:
							img_url = "file:jkhet/imgs/djed_p2_rot0.png";
							break;
						case 1:
						case 3:
							img_url = "file:jkhet/imgs/djed_p2_rot1.png";
							
					}
				}
			}		
			else if (a instanceof Obelisk) {
				if (a.player == 1) {
					switch(a.health) {
						case 1:
							img_url = "file:jkhet/imgs/obelisk_p1_h1.png";
							break;
						case 2:
							img_url = "file:jkhet/imgs/obelisk_p1_h2.png";
							
					}
				}
				else if (a.player == 2) {
					switch(a.health) {
						case 1:
							img_url = "file:jkhet/imgs/obelisk_p2_h1.png";
							break;
						case 2:
							img_url = "file:jkhet/imgs/obelisk_p2_h2.png";
							
					}
				}
			}	
			else if (a instanceof Pharaoh) {
				if (a.player == 1) { img_url = "file:jkhet/imgs/pharaoh_p1.png";}
				else if (a.player == 2) { img_url = "file:jkhet/imgs/pharaoh_p2.png";}
			}	
			else {continue; }
			BufferedImage buff_img;
			try{
				//buff_img = ImageIO.read(new URL("file:/Users/reecestevens/school/JKhet/src/jkhet/pyramid_p2_rot0.png"));
				buff_img = ImageIO.read(new URL(img_url));
				Image img = SwingFXUtils.toFXImage(buff_img,null);
				ImageView iv1 = new ImageView(img);
				iv1.setFitWidth(min-1);
				iv1.setPreserveRatio(true);
				iv1.setSmooth(true);
				board.add(iv1,a.x,a.y);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		masterGrid.add(board,0,0);
		Scene masterScene = new Scene(masterGrid,window_width,window_height);
		game.setScene(masterScene);
		game.show();
	}



	public static void initGui() {
		launch();
	}
}
