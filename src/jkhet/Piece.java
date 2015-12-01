package jkhet;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Master abstract class for board pieces.
 */
public abstract class Piece {

	/**
	 * mod(a,b) -- helper function for a more intuitive modulo
	 * @param a		Input number
	 * @param b		Base
	 * @return 		Modulus of a in base b
	 */
	public static int mod(int a, int b) {
		if (b <= 0) { return -1; } // INVALID CASE
		else if (a == 0) { return 0; }
		else if ((a < b) && (a > 0)) { return a; }
		else if (a > 0) {
			int c = a / b;
			return a - (c*b);
		}
		else {
			int c = (-a)/b;
			return (a + (b*(c+1)))%5;
		}
	}
	
	/**
	 * new_pos(dir,x,y) -- calculate a new position for a given position and direction.
	 * @param dir 	Direction to move
	 * @param x 	X location on board
	 * @param y 	Y location on board
	 * @return 		Integery array: [0] is x, [1] is y.
	 */
	private Integer[] new_pos(int dir, int x, int y) {
		int new_x = x;
		int new_y = y;
		switch(dir) {
			case 0:
				new_y -= 1;
				break;
			case 1:
				new_x += 1;
				new_y -= 1;
				break;
			case 2:
				new_x += 1;
				break;
			case 3:
				new_x += 1;
				new_y += 1;
				break;
			case 4:
				new_y += 1;
				break;
			case 5:
				new_x -= 1;
				new_y += 1;
				break;
			case 6:
				new_x -= 1;
				break;
			case 7:	
				new_x -= 1;
				new_y -= 1;

		}
		Integer[] retval = {new_x,new_y};
		return retval;
	}

	/**
	 * isOccupied(x,y) - Determine if a piece is already at
	 * a given location
	 * @param x		X coordinate on board
	 * @param y		Y coordinate on board
	 * @return 		A reference to the piece at that location
	 */
	public static Piece isOccupied(int x, int y) {
		for (Piece a : board_pieces) {
			if ((a.x == x) && (a.y == y)) { return a;}
		}	
		return null;
	}

	public String toString() { return ""; }
	
	/**
	 * getDir(x,y) -- get direction number (0-7) 
	 * relative to a piece based on a coordinate
	 * @param x		X coordinate of location
	 * @param y		Y coordinate of location
	 * @return 		Direction number (0-7) of this coordinate relative to the piece.
	 * 				Returns -1 if the location is not one spot away.
	 */
	public int getDir(int x, int y) {
		int delta_x = x - this.x;
		int delta_y = y - this.y;
		if (delta_x == -1) {
			switch (delta_y) {
				case -1:
					return 7;
				case 0:
					return 6;
				case 1:
					return 5;
				default:
					return -1;
			}
		} else if (delta_x == 0) {
			switch (delta_y) {
				case -1:
					return 0;
				case 0:
					return -1;
				case 1:
					return 4;
				default:
					return -1;
			}
		} else if (delta_x == 1) {
			switch (delta_y) {
				case -1:
					return 1;
				case 0:
					return 2;
				case 1:
					return 3;
				default:
					return -1;
			}
		} else {
			return -1;
		}
	}

	/**
	 * Move(int dir) -- move a piece 
	 * <p>When moving, you need to consider:
	 * 		1 - Is the direction valid?
	 * 		2 - Is there a piece already present?
	 * 			- If so, can the piece handle collisions? (Djed only)
	 * 		3 - Is the location a restricted space (red or silver)?
	 * </p>
	 * @param dir 	Direction of movement (0-7)
	 * @return 		Success (0) 
	 */
	public int move(int dir) throws InvalidMoveException{
		if ((dir < 0) || (dir > 7)) {
			// Invalid direction
			throw new InvalidMoveException("Invalid movement direction chosen, choose between 0-7.");
		}
		Integer[] newxy = new_pos(dir,this.x,this.y);
		// Check if position is valid
		if ((newxy[0] < 0) || (newxy[0] > Params.BOARD_WIDTH-1)) { 
				throw new InvalidMoveException("Can't move piece off the board.");			
		}
		if ((newxy[1] < 0) || (newxy[1] > Params.BOARD_HEIGHT-1)) {
				throw new InvalidMoveException("Can't move piece off the board.");			
		}
		if (newxy[0] == Params.BOARD_WIDTH - 1) {
			// Far right column: player 1 only
			if (player != 1) {
				throw new InvalidMoveException("Only Player 1 can move there.");
			}
		}
		else if (newxy[0] == 0) {
			// Far left column: player 2 only
			if (player != 2) {
				throw new InvalidMoveException("Only Player 2 can move there.");
			}
		}
		else if ((newxy[0] == 1) && ((newxy[1] == 0) || newxy[1] == Params.BOARD_HEIGHT-1)) {
			// Second to far left: top and bottom boxes are player 1 only
			if (player != 1) {
				throw new InvalidMoveException("Only Player 1 can move there.");
			}
		}
		else if ((newxy[0] == Params.BOARD_WIDTH-2) && ((newxy[1] == 0) || newxy[1] == Params.BOARD_HEIGHT-1)) {
			// Second to far right: top and bottom boxes are player 2 only
			if (player != 2) {
				throw new InvalidMoveException("Only Player 2 can move there.");
			}
		}
		Piece a = isOccupied(newxy[0], newxy[1]);
		if (a != null) {
			// Collision behavior depends on the piece
			if (this instanceof Djed) {
				swap(this,a);					
			}
			else {
				throw new InvalidMoveException("Another piece is already in that spot.");
			}
		}
		this.x = newxy[0];
		this.y = newxy[1];
		return 0;	
	}

	/**
	 * swap(a,b) -- swap the locations of two pieces on the board
	 * @param a 	First piece
	 * @param b 	Second piece
	 */	
	private void swap(Piece a, Piece b) {
		int tmp_x = a.x;
		int tmp_y = a.y;
		a.x = b.x;
		a.y = b.y;
		b.x = tmp_x;
		b.y = tmp_y;
	}

	/**
	 * rotate(boolean dir) -- rotate a piece CW or CCW
	 * @param dir 	Direction of rotation; CW (true) or CCW (false)
	 */
	public void rotate(boolean dir){
		if (dir) {
			/* CW */
			rot = mod((rot + 1) , 4);
		} else {
			/* CCW */
			rot = mod((rot - 1) , 4);
		}
	}

	/**
	 * boardMove(x,y,move_dir,player) -- initiate a move on the board and report 
	 * back if it is illegal.
	 * @param x		X location of piece to be moved.
	 * @param y 	Y location of piece to be moved.
	 * @param move_dir Direction the piece is to be moving (0-7)
	 * @param player 	Player initiating the move
	 */
	public static void boardMove(int x, int y, int move_dir, int player) throws InvalidMoveException {
		Piece a = isOccupied(x,y);
		// Piece doesn't exist on board
		if (a == null) { throw new InvalidMoveException("Piece doesn't exist."); }
		// Piece isn't the player's
		if (a.player != player) { throw new InvalidMoveException("This piece doesn't belong to you."); }
		try {
			a.move(move_dir);	
		} catch (InvalidMoveException e) {
			throw e;
		}
	}

	/**
	 * boardRotate(x,y,rot_dir,player) -- initiate a rotate on the board and report 
	 * back if it is illegal.
	 * @param x		X location of piece to be moved.
	 * @param y 	Y location of piece to be moved.
	 * @param rot_dir Rotation direction (CW (true) or CCW (false)) 
	 * @param player 	Player initiating the move
	 */
	public static void boardRotate(int x, int y, boolean rot_dir, int player) throws InvalidMoveException {
		Piece a = isOccupied(x,y);
		// Piece doesn't exist on board
		if (a == null) { throw new InvalidMoveException("Piece doesn't exist."); }
		// Piece isn't the player's
		if (a.player != player) { throw new InvalidMoveException("This piece doesn't belong to you."); }
		a.rotate(rot_dir);	
	}

	/**
	 * checkVictory() -- check if a player has won at the end of every turn.
	 * @return 		The player who won, or 0 if no player has yet won.
	 */
	public static int checkVictory() {
		boolean p1_pharaoh = false;
		boolean p2_pharaoh = false;
		for (Piece a : board_pieces) {
			if (a instanceof Pharaoh) {
				if (a.player == 1) { p1_pharaoh = true; }
				if (a.player == 2) { p2_pharaoh = true; }
			}
		}
		if (!p1_pharaoh) { 
			/* PLAYER 2 WINS */
			return 2;
		}
		else if (!p2_pharaoh) {
			/* PLAYER 1 WINS */
			return 1;
		}
		else {
			return 0;
		}
	}

	/**
	 * reflectDirection() -- determines output direction of the laser upon a collision.
	 *
	 * @param laser_direction 	The direction of entry for the laser, facing inward
	 * @return 		-1 if not a mirrored side and dead;
	 * 				-2 if not a mirrored side but alive;
	 * 				else, return exit direction of laser (0-3)
	 */
	public abstract int reflectDirection(int laser_direction);

	public static ArrayList<Piece> board_pieces = new ArrayList<>();

	public enum SetupType {
		CLASSIC,
		DYNASTY,
		IMHOTEP;
	}

	/**
	 * peekLaserRecursive(x,y,dir) -- peek at the laser without actually firing (recursive call)
	 * @param x 	X location of laser
	 * @param y 	Y location of laser
	 * @param dir 	Direction the laser is heading
	 * @return 		-1 if impact, 0 if error
	 */
	private static int peekLaserRecursive(int x, int y, int dir) {
		// Base case 1: Hit a wall
		if ((x < 0) || (x >= Params.BOARD_WIDTH)) { 
				return -1;
	   	}
		if ((y < 0) || (y >= Params.BOARD_HEIGHT)) { 
				return -1;
	   	}
		// Base case 2: Hit a piece in a non-mirror location
		Piece a = isOccupied(x,y);
		int new_dir = -1;
		if (a != null) {
			new_dir = a.reflectDirection(dir);
			String img_url = "";
			if (a.player == 1) {
			if (a instanceof Pyramid) {
				switch(a.rot) {
						case 0:
							img_url = "file:jkhet/imgs/pyramid_p1_rot0_laser.png";
							break;
						case 1:
							img_url = "file:jkhet/imgs/pyramid_p1_rot1_laser.png";
							break;
						case 2:
							img_url = "file:jkhet/imgs/pyramid_p1_rot2_laser.png";
							break;
						case 3:
							img_url = "file:jkhet/imgs/pyramid_p1_rot3_laser.png";
				}
			}
			else if (a instanceof Djed) {
				switch(a.rot) {
					case 0:
					case 2:
						if ((dir == 3)||(dir==2)) {
							img_url = "file:jkhet/imgs/djed_p1_rot0_laser_top.png";
						}
						else {
							img_url = "file:jkhet/imgs/djed_p1_rot0_laser_bottom.png";
						}
						break;
					case 1:
					case 3:
						if ((dir == 3)||(dir==0)) {
							img_url = "file:jkhet/imgs/djed_p1_rot1_laser_bottom.png";
						}
						else {
							img_url = "file:jkhet/imgs/djed_p1_rot1_laser_top.png";
						}
				}
			}
			}
			else {
			if (a instanceof Pyramid) {
				switch(a.rot) {
						case 0:
							img_url = "file:jkhet/imgs/pyramid_p2_rot0_laser.png";
							break;
						case 1:
							img_url = "file:jkhet/imgs/pyramid_p2_rot1_laser.png";
							break;
						case 2:
							img_url = "file:jkhet/imgs/pyramid_p2_rot2_laser.png";
							break;
						case 3:
							img_url = "file:jkhet/imgs/pyramid_p2_rot3_laser.png";
				}
			}
			else if (a instanceof Djed) {
				switch(a.rot) {
					case 0:
					case 2:
						if ((dir == 3)||(dir==2)) {
							img_url = "file:jkhet/imgs/djed_p2_rot0_laser_top.png";
						}
						else {
							img_url = "file:jkhet/imgs/djed_p2_rot0_laser_bottom.png";
						}
						break;
					case 1:
					case 3:
						if ((dir == 3)||(dir==0)) {
							img_url = "file:jkhet/imgs/djed_p2_rot1_laser_bottom.png";
						}
						else {
							img_url = "file:jkhet/imgs/djed_p2_rot1_laser_top.png";
						}
				}
			}
			}
			Image img = new Image(img_url);
			ImageView iv1 = new ImageView(img);
			iv1.setFitWidth(Gui.min-1);
			iv1.setPreserveRatio(true);
			iv1.setSmooth(true);
			Gui.board.add(iv1,x,y);

			if (new_dir == -1) {
				//a.health -= 1;
				//if (a.health == 0) { board_pieces.remove(a); }	
				return -1;
			}
		}
		else {
			String img_url = "";
			if ((x == 0) || ((x == Params.BOARD_WIDTH-2) && ((y == 0) || (y == Params.BOARD_HEIGHT-1))) ){
				if ((dir == 0) || (dir == 2)) {
					img_url = "file:jkhet/imgs/p2_space_laser_down.png";
				} else {
					img_url = "file:jkhet/imgs/p2_space_laser_across.png";
				}
			}
			else if ((x == Params.BOARD_WIDTH-1) || ((x == 1) && ((y == 0) || (y == Params.BOARD_HEIGHT-1)))) {
				if ((dir == 0) || (dir == 2)) {
					img_url = "file:jkhet/imgs/p1_space_laser_down.png";
				} else {
					img_url = "file:jkhet/imgs/p1_space_laser_across.png";
				}
			}
			else {
				if ((dir == 0) || (dir == 2)) {
					img_url = "file:jkhet/imgs/empty_space_laser_down.png";
				} else {
					img_url = "file:jkhet/imgs/empty_space_laser_across.png";
				}
			}
			Image img = new Image(img_url);
			ImageView iv1 = new ImageView(img);
			iv1.setFitWidth(Gui.min-1);
			iv1.setPreserveRatio(true);
			iv1.setSmooth(true);
			Gui.board.add(iv1,x,y);
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
	 * fireLaserRecursive(x,y,dir) -- Fire the laser (recursive call)
	 * @param x 	X location of laser
	 * @param y  	Y location of laser
	 * @param dir 	Direction laser is going
	 * @return 		-1 if impact, 0 if error
	 */
	private static int fireLaserRecursive(int x, int y, int dir) {
		// Base case 1: Hit a wall
		if ((x < 0) || (x > Params.BOARD_WIDTH)) { 
				return -1;
	   	}
		if ((y < 0) || (y > Params.BOARD_HEIGHT)) { 
				return -1;
	   	}
		// Base case 2: Hit a piece in a non-mirror location
		Piece a = isOccupied(x,y);
		int new_dir = -1;
		if (a != null) {
			new_dir = a.reflectDirection(dir);
			if (new_dir == -1) {
				a.health -= 1;
				if (a.health == 0) { board_pieces.remove(a); }	
				return -1;
			}
		}
		// Recursive step: find next laser location
		if (new_dir == -1) { new_dir = dir; }
		int ret = 0;
		switch(new_dir){
			case 0:
				ret = fireLaserRecursive(x,y-1,new_dir);
				break;
			case 1:
				ret = fireLaserRecursive(x+1,y,new_dir);
				break;
			case 2:
				ret = fireLaserRecursive(x,y+1,new_dir);
				break;
			case 3:
				ret = fireLaserRecursive(x-1,y,new_dir);
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
		if (player == 1) {
				peekLaserRecursive(Params.BOARD_WIDTH-1, Params.BOARD_HEIGHT-1, 0);
		}
		else {
				peekLaserRecursive(0,0,2);
		}
		
	}

	/**
	 * fireLaser(player) -- fire a player's laser
	 * @param player 	The player whose laser is being fired 
	 */
	public static void fireLaser(int player, boolean Gui) {
		if (player == 1) {
				fireLaserRecursive(Params.BOARD_WIDTH-1, Params.BOARD_HEIGHT-1, 0);
		}
		else {
				fireLaserRecursive(0,0,2);
		}
	}

	/**
	 * clearBoard() -- remove all pieces currently on the board
	 */
	private static void clearBoard() {
		board_pieces.clear();
	}

	/**
	 * add(x,y,player,rot) -- add a specified piece to the board
	 * @param x		X location on board
	 * @param y 	Y location on board
	 * @param player Player that owns the piece
	 * @param rot 	Rotational state of the piece
	 */
	private static int add(String piece_type, int x, int y, int player, int rot) {
		Class<?> ptype = null;
		try{
			piece_type = "jkhet." + piece_type;
			ptype = Class.forName(piece_type);
			Constructor<?> constr = null;
			constr = ptype.getConstructor();
			Object piece = null;
			piece = constr.newInstance();			
			((Piece) piece).x = x;
			((Piece) piece).y = y;
			((Piece) piece).player = player;
			((Piece) piece).rot = rot;
			board_pieces.add(((Piece) piece));
		} catch(Exception e) {
			e.printStackTrace();
			return -1;
		}
		return 0;	
	}

	/**
	 * setupBoard(mode) -- Clears board and sets up for another game
	 * in the selected mode.
	 *
	 * @param mode 		The game mode desired. 
	 */
	public static void setupBoard(SetupType mode){
		clearBoard();
		String ph = "Pharaoh";
		String p = "Pyramid";
		String o = "Obelisk";
		String d = "Djed";
		switch(mode) {
			case CLASSIC:
				// setup classic
				// row 0
				add(o,4,0,2,0);	
				add(ph,5,0,2,0);	
				add(o,6,0,2,0);	
				add(p,7,0,2,1);	
				// row 1
				add(p,2,1,2,2);
				// row 2
				add(p,3,2,1,3);
				// row 3
				add(p,0,3,2,0);
				add(p,2,3,1,2);
				add(d,4,3,2,0);
				add(d,5,3,2,1);
				add(p,7,3,2,1);
				add(p,9,3,1,3);
				// row 4
				add(p,0,4,2,1);
				add(p,2,4,1,3);
				add(d,4,4,1,1);
				add(d,5,4,1,0);
				add(p,7,4,2,0);
				add(p,9,4,1,2);
				// row 5
				add(p,6,5,2,1);
				// row 6
				add(p,7,6,1,0);
				// row 7
				add(p,2,7,1,3);
				add(o,3,7,1,0);
				add(ph,4,7,1,0);
				add(o,5,7,1,0);				
				break;
			case DYNASTY:
				// setup dynasty 
				// row 0
				add(p,4,0,2,2);
				add(o,5,0,2,0);
				add(p,6,0,2,1);
				// row 1
				add(ph,5,1,2,0);
				// row 2
				add(p,0,2,2,0);
				add(p,4,2,2,2);
				add(o,5,2,2,0);
				add(d,6,2,2,1);
				// row 3
				add(p,0,3,2,1);
				add(d,2,3,2,0);
				add(p,4,3,1,3);
				add(p,6,3,1,1);
				// row 4
				add(p,3,4,2,3);
				add(p,5,4,2,1);
				add(d,7,4,1,0);
				add(p,9,4,1,3);
				// row 5
				add(d,3,5,1,1);
				add(o,4,5,1,0);
				add(p,5,5,1,0);
				add(p,9,5,1,2);
				// row 6
				add(ph,4,6,1,0);
				// row 7
				add(p,3,7,1,3);
				add(o,4,7,1,0);
				add(p,5,7,1,0);
				break;

			case IMHOTEP:
				// setup imhotep
				// row 0
				add(o,4,0,2,0);	
				add(ph,5,0,2,0);	
				add(o,6,0,2,0);	
				add(d,7,0,2,1);	
				// row 2
				add(p,3,2,1,3);
				add(p,6,2,2,0);
				// row 3
				add(p,0,3,2,0);
				add(p,1,3,1,2);
				add(p,4,3,1,1);
				add(d,5,3,2,1);
				add(p,8,3,2,1);
				add(p,9,3,1,3);
				// row 4
				add(p,0,4,2,1);
				add(p,1,4,1,3);
				add(d,4,4,1,1);
				add(p,5,4,2,3);
				add(p,8,4,2,0);
				add(p,9,4,1,2);
				// row 5
				add(p,6,5,2,1);
				add(p,3,5,1,2);
				// row 7
				add(d,2,7,1,1);
				add(o,3,7,1,0);
				add(ph,4,7,1,0);
				add(o,5,7,1,0);				

				break;
			
		}	
	}

	// Position and Rotation state.	
	public int player;
	public int rot;
	public int x;
	public int y;
	public int health = 1;
	
}
