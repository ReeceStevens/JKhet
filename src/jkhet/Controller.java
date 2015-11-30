package jkhet;

import java.util.Scanner;

public class Controller {
	public static void main(String [] args) {
		cli();
	}
	
	/**
	 * cli() -- Initializes command line interface
	 */	
	public static void cli() {
		System.out.println("Welcome to JKhet!");
		System.out.print("Starting a new classic game...");
		Piece.setupBoard(Piece.SetupType.CLASSIC);
		System.out.print(" Done!\n");
		showBoard();
		Scanner k = new Scanner(System.in);
		int turn = 1;
		while(true) {
			String commands = k.nextLine();
			String [] arguments = commands.split(" ");
			switch (arguments[0]) {
				case "move":
					turn = Piece.mod((turn + 1),2);
					break;
				case "rotate":
					turn = Piece.mod((turn + 1),2);
					break;
				case "quit":
					System.out.println("Thanks for playing!");
					return;
				default: 
					System.out.println("Sorry, that's not a valid command.");	
			}
		}
	}
	

	private static void showBoard() {
		for (int i = 0; i < Params.BOARD_HEIGHT+2; i += 1) {
			for (int j = 0; j < Params.BOARD_WIDTH+2; j += 1) {
				if ((i == 0) || (i == Params.BOARD_HEIGHT+1)) { System.out.print("---"); continue; }
				if ((j == 0) || (j == Params.BOARD_WIDTH+1)) { System.out.print(" | "); continue; }
				boolean piece_set = false;
				for (Piece a : Piece.board_pieces) {
					if ((a.x == j-1) && (a.y == i-1)){
						System.out.print(" " + a.toString() + " ");
						piece_set = true;
					}
				}
				if (!piece_set) {
					System.out.print("   ");
				}
			}
			System.out.println("");
		}
	}
}
