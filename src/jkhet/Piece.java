package jkhet;

import java.util.ArrayList;

/**
 * Master abstract class for board pieces.
 */
public abstract class Piece {


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
	 * @return 		Space is occupied by a piece (true) or empty (false)
	 */
	private boolean isOccupied(int x, int y) {
		// TODO: finish this function.
		for (Piece a : board_pieces) {
			if ((a.x == x) && (a.y == y)) { return true;}
		}	
		return false;
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
		if (isOccupied(newxy[0], newxy[1])) {
			// Collision behavior depends on the piece
			// TODO: add djed swapping action
			// For all pieces except Djeds, collision = failed move.
			return -1;
		}

		
			
		return 0;	
	}

	/**
	 * rotate(boolean dir) -- rotate a piece CW or CCW
	 * @param dir 	Direction of rotation; CW (true) or CCW (false)
	 */
	public void rotate(boolean dir){
		if (dir) {
			/* CW */
			rot = (rot + 1) % 8;
		} else {
			/* CCW */
			// TODO: check Java's mod definition, make sure this behaves 
			// the way you are expecting.
			rot = (rot - 1) % 8;
		}
	}

	public static ArrayList<Piece> board_pieces; 

	// Position and Rotation state.	
	public int player;
	public int rot;
	public int x;
	public int y;
	
}
