package jkhet;

public class Pharaoh extends Piece {

	// Void constructor
	public Pharaoh() {
	}	

	public Pharaoh (int x, int y, int player, int rot) {
		this.x = x; 
		this.y = y;
		this.rot = rot;
		this.player = player;
	}

	public String toString() {
		String ret = "";
		if (player == 1) {
			ret = (char)27 + "[36m";
		}
		else {
			ret = (char)27 + "[31m";
		}
		ret += "§" + (char)27 + "[0m";
		return ret;
	}

	/**
	 * reflectDirection() -- determines output direction of the laser upon a collision.
	 *
	 * @param laser_direction 	The direction of entry for the laser, facing inward
	 * @return 		-1 if not a mirrored side and dead;
	 * 				-2 if not a mirrored side but alive;
	 * 				else, return exit direction of laser (0-3)
	 */
	public int reflectDirection(int laser_direction) {
		return -1;
	}
}
