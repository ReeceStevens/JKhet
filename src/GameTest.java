package jkhet;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

public class GameTest {

	/*UNIT TESTS*/
	
	@Test
	public void LaserKillTest() {
		Piece.board_pieces.clear();
		Piece.add("Pyramid",0,0,1,1);
		Piece.fireLaser(2);
		assertEquals(Piece.board_pieces.size(), 0);
	}
	
	@Test
	public void addPiece() {
		Piece.board_pieces.clear();
		Piece.add("Pyramid", 0, 0, 1, 1);
		assertEquals(Piece.board_pieces.size(),1);
	}
	
	@Test
	public void moveTest() {
		Piece.board_pieces.clear();
		
		Piece.add("Pyramid", 0, 0, 1, 1);
		try {
			Piece.boardMove(0, 0, 2, 1);
		} catch (InvalidMoveException e) {
			e.printStackTrace();
			fail("Incorrectly classified as invalid move");
		}
		for (Piece p : Piece.board_pieces){
			if (p.x == 1 && p.y == 0) {
				return;
			}
		}
		fail("Piece wasn't moved correctly.");
	}
	
	@Test
	public void rotateTest() {
		Piece.board_pieces.clear();
		Piece.add("Pyramid", 5, 5, 1, 1);
		try {
			Piece.boardRotate(5, 5, true, 1);
		} catch (InvalidMoveException e) {
			e.printStackTrace();
			fail("Incorrectly classified as invalid move");
		}
		for (Piece p : Piece.board_pieces){
			if (p.rot == 2) {
				return;
			}
		}
		fail("Piece wasn't moved correctly.");
	}
	
	@Test 
	public void swapTest() {
		Piece.board_pieces.clear();
		Piece.add("Djed", 5, 5, 1, 1);
		Piece.add("Pyramid", 5, 6, 1, 2);
		try {
			Piece.boardMove(5, 5, 4, 1);
		} catch (InvalidMoveException e) {
			e.printStackTrace();
			fail("Incorrectly classified as invalid move");
		}
		for (Piece p : Piece.board_pieces){
			if ((p instanceof Djed) && (p.y != 6)) {
				fail("Swap didn't work.");
			}			
			if ((p instanceof Pyramid) && (p.y != 5)) {
				fail("Swap didn't work.");
			}
		}
	}

	/*SYSTEM TESTS*/
	
	@Test
	public void startAndEndTest() {
		String game = "1\nquit\n";
		InputStream in = new ByteArrayInputStream(game.getBytes());
		Controller.cli(in);		    
	}
	
	@Test
	public void checkmateTest() {
		Piece.board_pieces.clear();
		String game = "1\nrotate 9 3 CCW\nmove 5 3 0\nmove 9 3 0\nrotate 5 2 CW\nmove 3 7 0\nN\n";
		InputStream in = new ByteArrayInputStream(game.getBytes());
		Controller.cli(in);		
		
	}
}
