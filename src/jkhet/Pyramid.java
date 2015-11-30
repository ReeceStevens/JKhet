package jkhet;

public class Pyramid extends Piece {

	// A rough approximation on the console of the two 
	// mirror angles (as best as unicode can provide)
	public String toString() {
		String ret = "";
		if (player == 1) {
			ret = (char)27 + "[36m";
		}
		else {
			ret = (char)27 + "[31m";
		}
		switch(rot) {
			case 0:
				ret = ret+"L";
				break;
			case 1:
				ret = ret+"r";
				break;
			case 2:
				ret = ret+"¬";
				break;
			case 3:
				ret =  ret+"j";
				break;
			default: // Should never be reached
				ret = ret+"P";
		}
		ret += (char)27 + "[0m";
		return ret;
	}

	// Void constructor
	public Pyramid() {
	}

	public Pyramid(int x, int y, int player, int rot) {
		this.x = x;
		this.y = y;
		this.rot = rot;	
		this.player = player;
	}	

	/**
	 * reflectDirection() -- determines output direction of the laser upon a collision.
	 *
	 * @param laser_direction 	The direction the laser is currently moving 
	 * @return 		-1 if not a mirrored side and dead;
	 * 				-2 if not a mirrored side but alive;
	 * 				else, return exit direction of laser (0-3)
	 */
	public int reflectDirection(int laser_direction) {
		laser_direction = Piece.mod(laser_direction+2,4);
		int relative_side = Piece.mod((laser_direction - rot) , 4);
		// Check if the laser hit a mirror.
		if (relative_side == 0) {
			return Piece.mod((1+rot) , 4);
		}
		else if (relative_side == 1) {
			return Piece.mod(rot , 4);
		}
		else {
			// Impacted by laser on non-mirrored side. Piece is dead.
			board_pieces.remove(this);
			return -1;
		}
	}
	
}
