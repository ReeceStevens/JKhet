package jkhet;

// TODO: allow the djed only to swap places with another piece in its move

public class Djed extends Piece {
	public Djed (int x, int y, int player, int rot) {
		this.x = x; 
		this.y = y;
		this.rot = rot;
		this.player = player;
	}

	public String toString() {
		boolean a = (rot == 0) || (rot == 2);
		String ret = "";
		if (player == 1) {
			ret = (char)27 + "[36m";
		}
		else {
			ret = (char)27 + "[31m";
		}
		if (a) {
			ret += "\\";
		}
		else {
			ret += "/";
		}
		ret += (char)27 + "[0m";
		return ret;
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
		boolean a = (rot == 0) || (rot == 2);
		if (a) {
			switch(laser_direction) {
				case 0:
					return 1;
				case 1:
					return 0;
				case 2:
					return 3;
				case 3:
					return 2;
			}
		}
		else {
			switch(laser_direction) {
				case 0:
					return 3;
				case 1:
					return 2;
				case 2:
					return 1;
				case 3:
					return 0;
			}
		}
		return -1; // Never reached	
	}	

}
