package jkhet;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Gui extends Application {
	private static int turn = 1;
	private static Piece selected = null;
	public static Stage game = null; 
	public static Stage startStage = null; 
	protected static GridPane board = null;
	private static GridPane controls = null;
	private static VBox moveOptions = new VBox();
	private static Text player = new Text();
	private static Text winner = new Text();
	private static Button peekAtLaser = null; 
	private static int window_height = 500;
	private static boolean peeking = false;
	protected static int min = window_height / Params.BOARD_HEIGHT;

	@Override
	public void start(Stage primaryStage) {
		startStage = primaryStage;
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
	 * drawBoard(laser) -- update the board 
	 * @param laser 0, don't display laser.
	 * 				1 or 2, display laser of player.
	 */
	public static void drawBoard(int laser) {
		board.getChildren().clear();
		for (int i = 0; i < Params.BOARD_WIDTH; i += 1) {
			for (int j = 0; j < Params.BOARD_HEIGHT; j += 1) {
			String img_url = "";
			if ((i == 0) || ((i == Params.BOARD_WIDTH-2) && ((j == 0) || (j == Params.BOARD_HEIGHT-1))) ){
				img_url = "file:./jkhet/imgs/p2_space.png";
			}
			else if ((i == Params.BOARD_WIDTH-1) || ((i == 1) && ((j == 0) || (j == Params.BOARD_HEIGHT-1)))) {
				img_url = "file:./jkhet/imgs/p1_space.png";
			}
			else {
				img_url = "file:./jkhet/imgs/empty_space.png";
			}
			Image img = new Image(img_url);
			ImageView iv1 = new ImageView(img);
			iv1.setFitWidth(min-1);
			iv1.setPreserveRatio(true);
			iv1.setSmooth(true);
			board.add(iv1,i,j);
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
			Image img = new Image(img_url);
			ImageView iv1 = new ImageView(img);
			iv1.setFitWidth(min-1);
			iv1.setPreserveRatio(true);
			iv1.setSmooth(true);
			board.add(iv1,a.x,a.y);
		}
		if (turn == 1) { player.setText("Player 1"); }
		else { player.setText("Player 2"); }	
		if (laser != 0) {
			Piece.peekLaser(laser);
		}
	}
	
	/**
	 * endGame(winner) -- Display the winner of the game
	 * @param win 	Player who won the game
	 */
	private static void endGame(int win) {
		turn = 0;
		winner.setFill(Color.DARKGOLDENROD);
		winner.setText("The winner is Player " + win + "!");	
		moveOptions.getChildren().clear();
		Button playAgain = new Button("Play Again");
		Button quit = new Button("Quit");
		moveOptions.getChildren().add(playAgain);
		moveOptions.getChildren().add(quit);
		playAgain.setOnAction(new EventHandler<ActionEvent>() {
			@Override		
			public void handle(ActionEvent event) {
				board.getChildren().clear();
				controls.getChildren().clear();
				game.close();			
			}
		});
		quit.setOnAction(new EventHandler<ActionEvent>() {
			@Override		
			public void handle(ActionEvent event) {
				board.getChildren().clear();
				controls.getChildren().clear();
				startStage.close();
				game.close();			
			}
		});
	}
	
	/**
	 * boardLaser(turn) -- launch the laser and show
	 * the path on the GUI 
	 * @param turn 		The player launching the laser
	 */
	private static void boardLaser(int turn) {
		Piece.fireLaser(turn, true);
		//moveOptions.getChildren().clear();
		//Button cont = new Button("Continue");
		//moveOptions.getChildren().add(cont);
		/*while(!cont.isPressed()){
			System.out.println("Waiting...");	
		}*/
		/*cont.setOnAction(new EventHandler<ActionEvent>() {
			@Override		
			public void handle(ActionEvent event) {
				moveOptions.getChildren().clear();
				drawBoard(0);
				return;
			}
		});*/
	}	

	/**
	 * launchGame() -- open game window and begin playing
	 */
	public static void launchGame() {
		int window_height = 500;
		int window_width = 1000;
		winner.setText("");
		turn = 1;
		moveOptions.getChildren().clear();
		game = new Stage();
		GridPane masterGrid = new GridPane();
		board = new GridPane();
		controls = new GridPane();	
		drawBoard(0);	
		masterGrid.add(board,0,0);
		controls.setHgap(10);
		controls.setVgap(10);
		controls.setPadding(new Insets(25,25,25,25));
		Label turnTitle = new Label("Turn: ");
		controls.add(turnTitle,0,0);
		if (turn == 1) { player.setText("Player 1"); }
		else { player.setText("Player 2"); }	
		controls.add(player,1,0);
		Text moveError = new Text();
		peekAtLaser = new Button("Peek Laser");
		controls.add(moveOptions,0,1);
		controls.add(peekAtLaser,1,1);
		controls.add(moveError,0,2);
		controls.add(winner,0,3);
		masterGrid.add(controls,1,0);		

		peekAtLaser.setOnAction(new EventHandler<ActionEvent>() {
			@Override		
			public void handle(ActionEvent event) {
				if (peeking) {
					drawBoard(0);
					peekAtLaser.setText("Peek Laser");
					peeking = false;
				} else {
					drawBoard(turn);
					peekAtLaser.setText("Done");
					peeking = true;
				}
			}
		});

		board.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (turn == 0) { return; }
				for (Node node : board.getChildren()) {
					//if (node instanceof ImageView) {
						if (node.getBoundsInParent().contains(e.getSceneX(), e.getSceneY())){
							int y = GridPane.getRowIndex(node);
							int x = GridPane.getColumnIndex(node);
							Piece p = Piece.isOccupied(x,y);
							if (selected != null) {
								// do move stuff
								int dir = selected.getDir(x,y);	
								try {
									Piece.boardMove(selected.x, selected.y,dir,turn);
									moveError.setText("");
									boardLaser(turn);
									//Piece.fireLaser(turn);
									turn = (turn == 1) ? 2 : 1;
									moveOptions.getChildren().clear();
									selected = null;
									drawBoard(0);
									peeking = false;
									peekAtLaser.setText("Peek Laser");
									int winner = Piece.checkVictory();
									if (winner != 0) {
										endGame(winner);
									}
									return;
								} catch (InvalidMoveException err) {
									moveError.setFill(Color.FIREBRICK);
									moveError.setText("This is an invalid move!");
									return;
								}
							}
							else {
								if (p == null) { return; }
								if (turn != p.player) { 
									moveError.setFill(Color.FIREBRICK);
									moveError.setText("This piece doesn't\nbelong to you!");
									return;
								}
								selected = p;
								Button rotate_cw = new Button("Rotate CW");
								Button rotate_ccw = new Button("Rotate CCW");
								rotate_cw.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										try {
											Piece.boardRotate(p.x,p.y,true, turn);
											boardLaser(turn);
											//Piece.fireLaser(turn);
											turn = (turn == 1) ? 2 : 1;
											selected = null;
											moveOptions.getChildren().clear();
											drawBoard(0);
											peeking = false;
											peekAtLaser.setText("Peek Laser");
											int winner = Piece.checkVictory();
											if (winner != 0) {
												endGame(winner);
											}
											return;
										} catch (InvalidMoveException err) {
											selected = null;
											moveError.setFill(Color.FIREBRICK);
											moveError.setText("This is an invalid move!");
											return;
											//err.printStackTrace();
										}
									}
								});
								rotate_ccw.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										try {
											Piece.boardRotate(p.x,p.y,false, turn);
											boardLaser(turn);
											//Piece.fireLaser(turn);
											turn = (turn == 1) ? 2 : 1;
											selected = null;
											moveOptions.getChildren().clear();
											drawBoard(0);
											peeking = false;
											peekAtLaser.setText("Peek Laser");
											int winner = Piece.checkVictory();
											if (winner != 0) {
												endGame(winner);
											}
											return;	
										} catch (InvalidMoveException err) {
											//err.printStackTrace();
											moveError.setFill(Color.FIREBRICK);
											moveError.setText("This is an invalid move!");
											selected = null;
											return;
										}
									}
								});
								moveOptions.getChildren().clear();
								moveOptions.getChildren().add(rotate_cw);
								moveOptions.getChildren().add(rotate_ccw);
								moveError.setText("");
								return;
							}
						}
					}
				//}
			}
		});	
			
		Scene masterScene = new Scene(masterGrid,window_width,window_height);
		game.setScene(masterScene);
		game.show();
	}


	/**
	 * initGui() -- launches the JavaFX main application window
	 */
	public static void initGui() {
		launch();
	}
}
