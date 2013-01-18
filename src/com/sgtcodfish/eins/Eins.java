package com.sgtcodfish.eins;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;

import com.sgtcodfish.eins.AIPlayer.AIDifficulty;

public class Eins {
	public static final String PLAYER_NAME_DEFAULT = "Player";
	
	public static void main(String[] args) {
		ConsoleIOHandler io = new ConsoleIOHandler();
		
		io.println("Welcome to EINS! Please enter your name:");
		// readString will cause a flush here.
		String nname = io.readString();
		
		if(nname.compareTo("") == 0) {
			nname = PLAYER_NAME_DEFAULT;
		}
		
		int userInput = -1;
		int reps = -1;
		AIDifficulty diff = AIDifficulty.REGULAR;
		
		do {
			io.println("Choose a way to play:");
			io.println("Enter 1 to: Play a single quick game of EINS against 5 computer players of REGULAR difficulty!");
			io.println("Enter 2 to: Play a single quick game of EINS against 5 computer players of BEST difficulty!");
			io.println("Enter 3 to: Play a 3-game series of EINS against 5 computer players of REGULAR difficulty!");
			io.println("Enter 4 to: Play a 3-game series of EINS against 5 computer players of BEST difficulty!");
			io.println("Enter 5 to: Play an ultimate 5-game tournament against 5 computer players of BEST difficulty!");
			// readInt will cause a flush here.
			
			try {
				userInput = io.readInt();
			} catch(InputMismatchException ime) {
				io.println("Only type in a number, nothing else!\n");
				userInput = -1;
			} catch(NoSuchElementException nsee) {
				//io.printErrorln("NSEE: " + nsee + "\nuserInput = " + userInput);
				userInput = -1;
			}
			
			if(userInput > 0 && userInput < 6) {
				break;
			} else if(userInput == 10419) {
				break;
			}
		} while(true);
		
		/*
		 * The following is ugly, but corresponds to the menu options hardcoded above. There are more elegant solutions than this certainly,
		 * but this gets the job done and gets the game shipped.
		 */
		if(userInput == 1) {
			reps = 1;
			diff = AIDifficulty.REGULAR;
		} else if(userInput == 2) {
			reps = 1;
			diff = AIDifficulty.BEST;
		} else if(userInput == 3) {
			reps = 3;
			diff = AIDifficulty.REGULAR;
		} else if(userInput == 4) {
			reps = 3;
			diff = AIDifficulty.BEST;
		} else if(userInput == 5) {
			reps = 5;
			diff = AIDifficulty.BEST;
		} else if(userInput == 10419) {
			reps = 99;
			diff = AIDifficulty.REGULAR;
			io.println("-----\nCongratulations on finding the hidden game mode! In this mode, 6 computers will play against each other 99 times.\n" +
					"\"Special Computer\" is BEST difficulty, while the others are REGULAR.\nEnjoy and see the difference between playing good and bad!\n-----\n\n"
					+ "The game will start in 10 seconds!");
			
			io.doDelay(10000);
		}
		
		ScoreList slist = new ScoreList();
		
		for(int i = 0; i < reps; i++) {
			io.println("Game #" + (i+1) + ":");
			io.flush();
			Table table = null;
			
			if(userInput != 10419) {
				table = new Table(io, nname, 5, diff);
			} else {				
				table = new Table(io, null, 5, diff);
			}
			
			if(table.mainLoop(slist)) { // someone requested we finish early if true
				slist.printScores(io);
				break;
			} else {
				slist.printScores(io);
			}
			
		}
		
		io.println("Final Scores:\n");
		slist.printScoresFinal(io);
		
		io.println("\nThanks for playing!");
		io.flush();
		io.readString(true);
	}
}
