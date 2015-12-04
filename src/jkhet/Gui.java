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
import java.util.HashSet;


/**
 * Class controlling all interface requirements of the game.
 */
public class Gui extends Application {
	private static HashSet<Integer[]> visited_locations = new HashSet<>();
	private static int turn = 1;
	private static Piece selected = null;
	public static Stage game = null; 
	public static Stage startStage = null; 
	protected static GridPane board = null;
	private static GridPane controls = null;
	private static VBox moveOptions = new VBox();
	private static Text player = new Text();
	private static Text winner = new Text();
	private static Text help = new Text();
	private static Button peekAtLaser = null; 
	private static int window_height = 480;
	private static int window_width = 1000;
	private static boolean peeking = false;
	protected static int min = (window_height / Params.BOARD_HEIGHT)+1;

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
	
		Label p1 = new Label("Blue: ");
		grid.add(p1,0,row);
		ObservableList<String> p1_options = FXCollections.observableArrayList(
			"Human",
			"Computer"
		);
		final ComboBox<String> p1Options = new ComboBox<>(p1_options);
		p1Options.setValue("Human");
		grid.add(p1Options,1,row);
		row ++;

		Label p2 = new Label("Red: ");
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

	private static boolean inBounds(int x, int y) {
		if ((x < 0) || (x >= Params.BOARD_WIDTH) || (y < 0) || (y >= Params.BOARD_HEIGHT)) {
			return false;
		}
		else { return true; }
	}

	/**
	 * spaceValid(player) -- helper function to tell if a space is restricted for this player.
	 * @param x 		X location of space
	 * @param y 		Y location of space
	 * @param player 	The player attempting the move
	 * @return 			True if move is okay, false if not.
	 */
	private static boolean spaceValid(int x, int y, int player) {
		if ((x == 0) || ((x == Params.BOARD_WIDTH-2) && ((y == 0) || (y == Params.BOARD_HEIGHT-1))) ){
			// P2 space
			return player == 2;
		}
		else if ((x == Params.BOARD_WIDTH-1) || ((x == 1) && ((y == 0) || (y == Params.BOARD_HEIGHT-1)))) {
			// P1 space
			return player == 1;
		}
		else {
			return true;
			// Empty space
		}
	}

	/**
	 * putPieceIcon(a,modifier) -- place the appropriate icon for a piece on the board.
	 * @param a 	The piece to be displayed
	 * @param modifier	0 for none, 1 for selected, 2 for laser (generic, or bottom for Djed), 3 for laser (top, 
	 * 					only used for Djed), 4 for laser (both sides, only used for Djed)
	 */
	protected static void putPieceIcon(Piece a, int modifier) {
			String img_url = "file:./jkhet/imgs/";
			// Determine piece type
			if (a instanceof Obelisk) { 
				img_url += "obelisk_"; 
				// Player
				if (a.player == 1) { img_url += "p1_"; }
				else { img_url += "p2_"; }
				// State
				if (a.health == 2) { img_url += "h2"; }
				else { img_url += "h1"; }
				// Modifier
				switch(modifier) {
					case 0:
					case 2:
						img_url += ".png";
						break;
					case 1:
						img_url += "_selected.png";
						break;
					default:
						img_url += ".png";
				}	
				
			}
			else if (a instanceof Pyramid) { 
				img_url += "pyramid_";
				// Player
				if (a.player == 1) { img_url += "p1_"; }
				else { img_url += "p2_"; }
				// State
				switch(a.rot) {
					case 0:
						img_url += "rot0";
						break;
					case 1:
						img_url += "rot1";
						break;
					case 2:
						img_url += "rot2";
						break;
					case 3:
						img_url += "rot3";
						break;
				}
				// Modifier
				switch(modifier) {
					case 0:
						img_url += ".png";
						break;
					case 1:
						img_url += "_selected.png";
						break;
					case 2:
						img_url += "_laser.png";
						break;
					default:
						img_url += ".png";
				}	
			}
			else if (a instanceof Djed) { 
				img_url += "djed_";
				// Player
				if (a.player == 1) { img_url += "p1_"; }
				else { img_url += "p2_"; }
				// State
				switch(a.rot) {
					case 0:
					case 2:
						img_url += "rot0";
						break;
					case 1:
					case 3:
						img_url += "rot1";
						break;
				}
				// Modifier
				switch(modifier) {
					case 0:
						img_url += ".png";
						break;
					case 1:
						img_url += "_selected.png";
						break;
					case 2:
						img_url += "_laser_bottom.png";
						break;
					case 3:
						img_url += "_laser_top.png";
						break;
					case 4:
						img_url += "_laser_both.png";
						break;
					default:
						img_url += ".png";
				}	
			}
			else if (a instanceof Pharaoh) { 
				img_url += "Pharaoh_";
				// Player
				if (a.player == 1) { img_url += "p1"; }
				else { img_url += "p2"; }
				// Modifier
				switch(modifier) {
					case 0:
						img_url += ".png";
						break;
					case 1:
						img_url += "_selected.png";
						break;
					default:
						img_url += ".png";
				}
		   	}
			else {return;}

			Image img = new Image(img_url);
			ImageView iv1 = new ImageView(img);
			iv1.setFitWidth(min-1);
			iv1.setPreserveRatio(true);
			iv1.setSmooth(true);
			board.add(iv1,a.x,a.y);
		}

	/**
	 * putSpaceIcon(x,y,mod_state) -- place the proper empty space icon on the board
	 * @param x 	X coordinate of space
	 * @param y 	Y coordinate of space
	 * @param mod_state 	0 if no mod, 1 if selected, 2 if laser down, 3 if laser across, 4 if laser crossed
	 */
	protected static void putSpaceIcon(int x, int y, int mod_state) {
		String img_url = "";
		if (inBounds(x,y)) { 	
			if ((x == 0) || ((x == Params.BOARD_WIDTH-2) && ((y == 0) || (y == Params.BOARD_HEIGHT-1))) ){
				img_url += "file:./jkhet/imgs/p2_space";
				//img_url += "file:./jkhet/imgs/p2_space_valid_move.png";
			}
			else if ((x == Params.BOARD_WIDTH-1) || ((x == 1) && ((y == 0) || (y == Params.BOARD_HEIGHT-1)))) {
				img_url += "file:./jkhet/imgs/p1_space";
				//img_url = "file:./jkhet/imgs/p1_space_valid_move.png";
			}
			else {
				img_url += "file:./jkhet/imgs/empty_space";
				//img_url = "file:./jkhet/imgs/empty_space_valid_move.png";
			}
			switch (mod_state) {
				case 0: 
					img_url += ".png";
					break;
				case 1:
					img_url += "_valid_move.png";
					break;
				case 2:
					img_url += "_laser_down.png";
					break;
				case 3:
					img_url += "_laser_across.png";
					break;
				case 4: 
					img_url += "_laser_crossed.png";
					break;
				default:
					img_url += ".png";
			}
			Image img = new Image(img_url);
			ImageView iv1 = new ImageView(img);
			iv1.setFitWidth(min-1);
			iv1.setPreserveRatio(true);
			iv1.setSmooth(true);
			board.add(iv1,x,y);
		}
	}

	private static void highlightValidMoves(Piece p, int x, int y) {
		for (int i = -1; i < 2; i ++) {
			for (int j = -1; j < 2; j ++) {
				if (p instanceof Djed) {
				if (inBounds(x+i,y+j)) { 	
					Piece a = Piece.isOccupied(x+i,y+j);
					if (a == null) {
						putSpaceIcon(x+i,y+j,1);
					} else {
						putPieceIcon(a,1);
					}
				}
				} else {
				if ((Piece.isOccupied(x+i,y+j) == null) && inBounds(x+i,y+j) && spaceValid(x+i,y+j,p.player)) { 	
					putSpaceIcon(x+i,y+j,1);
				}
				}
			}
		}
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
				putSpaceIcon(i,j,0);
			}
		}
		// Draw all Pieces.
		for (Piece a : Piece.board_pieces) {
			putPieceIcon(a,0);
		}
		if (turn == 1) { player.setText("Blue Player"); }
		else { player.setText("Red Player"); }	
		if (laser != 0) {
			peekLaser(laser);
		}
	}
	
	/**
	 * endGame(winner) -- Display the winner of the game
	 * @param win 	Player who won the game
	 */
	private static void endGame(int win) {
		turn = 0;
		help.setText("");
		if (win == 1) { 
			winner.setFill(Color.CYAN);
			winner.setText("The winner is Blue Player!" );	
		} else {
			winner.setFill(Color.RED);
			winner.setText("The winner is Red Player!");	

		}
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
		Piece.fireLaser(turn);
	}

	/**
	 * peekLaserRecursive(x,y,dir) -- peek at the laser without actually firing (recursive call)
	 * @param x 	X location of laser
	 * @param y 	Y location of laser
	 * @param dir 	Direction the laser is heading
	 * @return 		-1 if impact, 0 if error
	 */
	private static int peekLaserRecursive(int x, int y, int dir) {
		Integer[] this_location = {x,y};
		boolean wasHere = false;
		// Base case 1: Hit a wall
		if ((x < 0) || (x >= Params.BOARD_WIDTH)) { 
				return -1;
	   	}
		if ((y < 0) || (y >= Params.BOARD_HEIGHT)) { 
				return -1;
	   	}
		// Base case 2: Hit a piece in a non-mirror location
		Piece a = Piece.isOccupied(x,y);
		int new_dir = -1;
		if (a != null) {
			new_dir = a.reflectDirection(dir);
			if (a instanceof Djed) {
				/*for (Integer[] l : visited_locations) {
					if ((l[0] == this_location[0]) && (l[1] == this_location[1])) {
						Gui.putPieceIcon(a,4);
						wasHere = true;
					}
				}*/

				if (visited_locations.contains(this_location)) {
						Gui.putPieceIcon(a,4);
						wasHere = true;
				}
				if (!wasHere) {
				if (a.rot == 0 || a.rot == 2) {
					if (dir == 3 || dir == 2) { Gui.putPieceIcon(a,3); }
					else { Gui.putPieceIcon(a,2); }
				} else {
					if (dir == 1 || dir == 2) { Gui.putPieceIcon(a,3); }
					else { Gui.putPieceIcon(a,2); }
				}
				visited_locations.add(this_location);
				}
			}
			else if (new_dir == -1) {
				return -1;
			}
			else {
				Gui.putPieceIcon(a,2);
			}
		}
		else {
			for (Integer[] l : visited_locations) {
				if ((l[0] == this_location[0]) && (l[1] == this_location[1])) {
					wasHere = true;
					Gui.putSpaceIcon(x,y,4);
				}
			}
			if (!wasHere) {
				visited_locations.add(this_location);
				if ((dir == 0) || (dir == 2)) {
					Gui.putSpaceIcon(x,y,2);
				} else { 
					Gui.putSpaceIcon(x,y,3);
				}
			}
		}		
		// Recursive step: find next laser location
		if (new_dir == -1) { new_dir = dir; }
		int ret = 0;
		switch(new_dir){
			case 0:
				ret = peekLaserRecursive(x,y-1,new_dir);
				break;
			case 1:
				ret = peekLaserRecursive(x+1,y,new_dir);
				break;
			case 2:
				ret = peekLaserRecursive(x,y+1,new_dir);
				break;
			case 3:
				ret = peekLaserRecursive(x-1,y,new_dir);
				break;
		}
		if (ret == -1) { return -1; }
		else { return 0; }
	}

	/**
	 * peekLaser(player) -- look at a player's laser without actually firing
	 * @param player 	The player whose laser is being viewed
	 */
	public static void peekLaser(int player) {
		visited_locations.clear();
		if (player == 1) {
				peekLaserRecursive(Params.BOARD_WIDTH-1, Params.BOARD_HEIGHT-1, 0);
		}
		else {
				peekLaserRecursive(0,0,2);
		}
	}

	/**
	 * launchGame() -- open game window and begin playing
	 */
	public static void launchGame() {
		winner.setText("");
		help.setText("Select a piece to move.");
		turn = 1;
		moveOptions.getChildren().clear();
		game = new Stage();
		GridPane masterGrid = new GridPane();
		board = new GridPane();
		controls = new GridPane();	
		drawBoard(0);	
		masterGrid.add(board,0,0);
		controls.setHgap(10);
		controls.setVgap(20);
		controls.setPadding(new Insets(25,25,25,25));
		Label turnTitle = new Label("Turn: ");
		controls.add(turnTitle,0,0);
		if (turn == 1) { player.setText("Blue Player"); }
		else { player.setText("Red Player"); }	
		controls.add(player,1,0);
		Text moveError = new Text();
		peekAtLaser = new Button("Peek Laser");
		controls.add(moveOptions,0,1);
		controls.add(peekAtLaser,1,1);
		controls.add(moveError,0,2);
		controls.add(winner,0,3);
		controls.add(help,0,5);
		masterGrid.add(controls,1,0);		

		peekAtLaser.setOnAction(new EventHandler<ActionEvent>() {
			@Override		
			public void handle(ActionEvent event) {
				selected = null;
				help.setText("Select a piece to move.");
				moveOptions.getChildren().clear();
				moveError.setText("");
				if (peeking) {
					drawBoard(0);
					peekAtLaser.setText("Peek Laser");
					peeking = false;
				} else {
					drawBoard(turn);
					peekAtLaser.setText("Done Peeking");
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
									help.setText("Select a piece to move.");
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
									moveOptions.getChildren().clear();
									selected = null;
									help.setText("Select a piece to move.");
									drawBoard(0);
									peeking = false;
									peekAtLaser.setText("Peek Laser");
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
							
								// Cancel the laser preview and draw the possible moves	
								peeking = false;
								peekAtLaser.setText("Peek Laser");
								//drawBoard(0);
								putPieceIcon(p,1);
								highlightValidMoves(p,p.x,p.y);

								help.setText("Click where you want to move,\nor rotate CW/CCW.");
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
											help.setText("Select a piece to move.");
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
											help.setText("Select a piece to move.");
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
