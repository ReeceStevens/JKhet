package jkhet;

public class Controller {
	public static void main(String [] args) {
		showBoard();		
		Piece a = new Pyramid(0,0,1,0);
		Piece.board_pieces.add(a);
		Piece b = new Pyramid(1,1,2,0);
		Piece.board_pieces.add(b);
		Piece c = new Pyramid(2,2,1,0);
		Piece.board_pieces.add(c);
		Piece d = new Djed(3,3,1,0);
		Piece.board_pieces.add(d);
		Piece e = new Djed(3,5,2,1);
		Piece.board_pieces.add(e);
		Piece f = new Obelisk(1,7,1,0);
		Piece.board_pieces.add(f);
		showBoard();		
		c.rotate(false);
		Piece.board_pieces.remove(b);
		showBoard();

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
