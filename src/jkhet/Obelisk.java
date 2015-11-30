package jkhet;

// TODO: Split the obelisk in a turn? figure out how to implement that. 

public class Obelisk extends Piece {
	private boolean stacked = true;
	

	public String toString() {
		String ret = "";
		if (player == 1) {
			ret = (char)27 + "[36m";
		}
		else {
			ret = (char)27 + "[31m";
		}
		if (stacked) {
			ret += "•";
		} else { ret += "º"; }
		ret += (char)27 + "[0m";
		return ret;
	}

	public Obelisk (int x, int y, int player, int rot) {
		this.x = x; 
		this.y = y;
		this.rot = rot;
		this.player = player;
	}
	/**
	 * reflectDirection() -- determines output direction of the laser upon a collision.
	 * Notable for Djeds is that there are only two effective rotational states.
	 *
	 * @param laser_direction 	The direction of entry for the laser, facing inward
	 * @return 		-1 if not a mirrored side and dead;
	 * 				-2 if not a mirrored side but alive;
	 * 				else, return exit direction of laser (0-3)
	 */
	public int reflectDirection(int laser_direction) {
		if (stacked) { stacked = false; return -2; }
		else { return -1;}
	}

}
