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
		Scanner k = new Scanner(System.in);
		while(true) {
		System.out.println("Welcome to JKhet!");
		System.out.print("Starting a new classic game...");
		Piece.setupBoard(Piece.SetupType.CLASSIC);
		System.out.print(" Done!\n");
		showBoard();
		int turn = 1;
		while(true) {
			System.out.printf("Player %d > ", turn);
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
					// The move was successfully completed.
					// Fire the laser at the end of every turn
					Piece.fireLaser(turn);
					if (turn == 1) { turn = 2; }
					else { turn = 1; }	
					break;
				case "rotate":
					if (arguments.length != 4){
						System.out.println("Invalid command. Proper syntax is: rotate <x> <y> <CW/CCW>");
						continue;
					}
					x = 0; 
					y = 0;
					boolean rot_dir = true;
					if (arguments[3].equals("CW")) { rot_dir = true; }
					else if (arguments[3].equals("CCW")) { rot_dir = false; }
					else { System.out.println("Invalid rotation direction, choose either CW or CCW."); continue; }
					try {
						x = Integer.parseInt(arguments[1]);
						y = Integer.parseInt(arguments[2]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					try {
						Piece.boardRotate(x,y,rot_dir,turn);
					} catch (InvalidMoveException e) {
						e.printStackTrace();
						continue;
					}
					// The move was successfully completed.
					// Fire the laser at the end of every turn
					Piece.fireLaser(turn);
					if (turn == 1) { turn = 2; }
					else { turn = 1; }	
					break;
				case "show":
					showBoard();
					break;
				case "quit":
					System.out.println("Thanks for playing!");
					k.close();
					return;
				default: 
					System.out.println("Sorry, that's not a valid command.");	
			}
			int win = Piece.checkVictory();
			if (win == 1) {
				System.out.println("Player 1 wins!");
				break;
			}
			else if (win == 2) {
				System.out.println("Player 2 wins!");
				break;
			}
		}
		System.out.print("Play again? [Y/N] ");
		String response = k.nextLine();
		if (response.equals("Y")) { 
			continue;
		}
		else {
			k.close();
			return;
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
