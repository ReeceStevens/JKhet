package jkhet;

/**
 * Error encompassing all illegal moves
 */

public class InvalidMoveException extends Exception {
	
	String move_error;

	public InvalidMoveException(String move_error) {
		this.move_error = move_error;
	}

	public String toString() {
		return "InvalidMoveException: " + move_error;
	}
}
