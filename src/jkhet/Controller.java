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
					if (arguments.length != 4){
						System.out.println("Invalid command. Proper syntax is: move <x> <y> <dir>");
						continue;
					}
					// Check if x and y are valid
					// (i.e. it's on the board, there's a piece, and it's that player's piece
					int x = 0;
					int y = 0; 
					int dir = 0;
					try{
						x = Integer.parseInt(arguments[1]);
						y = Integer.parseInt(arguments[2]);
						dir = Integer.parseInt(arguments[3]);
					} catch (NumberFormatException e) {
						System.out.println("Invalid command. Proper syntax is: move <x> <y> <dir>");
						continue;
					}

					if ((x < 0) || (x > Params.BOARD_WIDTH) || (y < 0) || (y > Params.BOARD_HEIGHT)) { 
						System.out.println("That position is invalid. Please try another position.");
						continue;			
					}
					try{
						Piece.boardMove(x,y,dir,turn);
					} catch (InvalidMoveException e) {
						e.printStackTrace();
						continue;
					}
					turn = Piece.mod((turn + 1),2);
					Piece.fireLaser(1);
					break;
				case "rotate":
					turn = Piece.mod((turn + 1),2);
					break;
				case "quit":
					System.out.println("Thanks for playing!");
					k.close();
					return;
				default: 
					System.out.println("Sorry, that's not a valid command.");	
			}
		}
	}
	

	private static void showBoard() {
		for (int i = 0; i < Params.BOARD_HEIGHT+2; i += 1) {
			for (int j = 0; j < Params.BOARD_WIDTH+2; j += 1) {
				if ((i == 0) || (i == Params.BOARD_HEIGHT+1)) { 
						if ((j == 0) || (j == Params.BOARD_WIDTH+1)){ System.out.printf("---");continue;}
						System.out.printf("-%d-",j-1); continue; }
				if ((j == 0) || (j == Params.BOARD_WIDTH+1)) { System.out.printf(" %d ",i-1); continue; }
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
