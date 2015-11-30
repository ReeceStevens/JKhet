package jkhet;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

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
	private static Piece isOccupied(int x, int y) {
		// TODO: finish this function.
		for (Piece a : board_pieces) {
			if ((a.x == x) && (a.y == y)) { return a;}
		}	
		return null;
	}

	public String toString() { return ""; }

	/**
	 * Move(int dir) -- move a piece 
	 * <p>When moving, you need to consider:
	 * 		1 - Is the direction valid?
	 * 		2 - Is there a piece already present?
	 * 			- If so, can the piece handle collisions? (Djed only)
	 * 		3 - Is the location a restricted space (red or silver)?
	 * </p>
	 * @param dir 	Direction of movement (0-7)
	 * @return 		Success (0) or failure (-1)
	 */
	public int move(int dir){
		if ((dir < 0) || (dir > 7)) {
			// Invalid direction
			// TODO: Throw some error
			System.out.println("Invalid Direction!");
			return -1;
		}
		Integer[] newxy = new_pos(dir,this.x,this.y);
		// Check if position is valid
		if ((newxy[0] < 0) || (newxy[0] > Params.BOARD_WIDTH-1)) { return -1; }
		if ((newxy[1] < 0) || (newxy[1] > Params.BOARD_HEIGHT-1)) { return -1; }
		if (newxy[0] == Params.BOARD_WIDTH - 1) {
			// Far right column: player 1 only
			if (player != 1) {
				return -1;
			}
		}
		else if (newxy[0] == 0) {
			// Far left column: player 2 only
			if (player != 2) {
				return -1;
			}
		}
		else if ((newxy[0] == 1) && ((newxy[1] == 0) || newxy[1] == Params.BOARD_HEIGHT-1)) {
			// Second to far left: top and bottom boxes are player 1 only
			if (player != 1) {
				return -1;
			}
		}
		else if ((newxy[0] == Params.BOARD_WIDTH-2) && ((newxy[1] == 0) || newxy[1] == Params.BOARD_HEIGHT-1)) {
			// Second to far right: top and bottom boxes are player 2 only
			if (player != 2) {
				return -1;
			}
		}
		Piece a = isOccupied(newxy[0], newxy[1]);
		if (a != null) {
			// Collision behavior depends on the piece
			if (this instanceof Djed) {
				swap(this,a);					
			}
			else {
				return -1;
			}
		}
		return 0;	
	}

	/**
	 * swap(a,b) -- swap the locations of two pieces on the board
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
	 * @return 		Success (0) or failure (-1)
	 */
	public static int boardMove(int x, int y, int move_dir, int player) throws InvalidMoveException {
		Piece a = isOccupied(x,y);
		// Piece doesn't exist on board
		if (a == null) { throw new InvalidMoveException("Piece doesn't exist."); }
		// Piece isn't the player's
		if (a.player != player) { throw new InvalidMoveException("This piece doesn't belong to you."); }
		return a.move(move_dir);	
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

	private static int fireLaserRecursive(int x, int y, int dir) {
		// Base case 1: Hit a wall
		// TODO: remove these debug statements
		if ((x < 0) || (x > Params.BOARD_WIDTH)) { 
				System.out.printf("Hit the wall at %d, %d!\n",x,y);			
				return -1;
	   	}
		if ((y < 0) || (y > Params.BOARD_HEIGHT)) { 
				System.out.printf("Hit the wall at %d, %d!\n",x,y);			
				return -1;
	   	}
		// Base case 2: Hit a piece in a non-mirror location
		Piece a = isOccupied(x,y);
		int new_dir = -1;
		if (a != null) {
			new_dir = a.reflectDirection(dir);
			if (new_dir == -1) {
				// TODO: handle death? exception of obelisk?
				// TODO: Remove this debug statement
				System.out.printf("Piece at %d, %d was hit!\n", x,y);
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

	public static void fireLaser(int player) {
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
		// TODO: finish inputing the other game setups
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
				add(d,4,4,2,1);
				add(d,5,4,2,0);
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
				break;
			case IMHOTEP:
				// setup imhotep
				break;
		}	
	}

	// Position and Rotation state.	
	public int player;
	public int rot;
	public int x;
	public int y;
	
}
