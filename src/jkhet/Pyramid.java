package jkhet;

public class Pyramid extends Piece {

	// Void Constructor
	public Pyramid(int x, int y, int player, int rot) {
		this.x = x;
		this.y = y;
		this.rot = rot;	
		this.player = player;
		board_pieces.add(this);
	}	

	/**
	 * reflectDirection() -- determines output direction of the laser upon a collision.
	 *
	 * @param p		The piece being hit by the laser
	 * @param laser_direction 	The direction of entry for the laser, facing inward
	 * @return 		-1 if not a mirrored side; else, return exit direction of laser (0-3)
	 */
	public int reflectDirection(Piece p, int laser_direction) {
		int relative_side = (laser_direction + rot) % 4;
		// Check if the laser hit a mirror.
		if (relative_side == 0) {
			return (1 - rot) % 4;
		}
		if (relative_side == 1) {
			return rot % 4;
		}
		else {
			// Impacted by laser on non-mirrored side. Piece is dead.
			board_pieces.remove(this);
			return -1;
		}
	}
	
}
