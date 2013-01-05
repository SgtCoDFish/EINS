package com.sgtcodfish.eins;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.sgtcodfish.eins.AIPlayer.AIDifficulty;

public class Eins {
	public static final String PLAYER_NAME_DEFAULT = "Player";
	
	public static void main(String[] args) {
		System.out.println("Welcome to EINS! Please enter your name:");
		Scanner scanner = new Scanner(System.in);
		String nname = scanner.nextLine();
		
		if(nname.compareTo("") == 0) {
			nname = PLAYER_NAME_DEFAULT;
		}
		
		int userInput = -1;
		int reps = -1;
		AIDifficulty diff = AIDifficulty.REGULAR;
		
		do {
			System.out.println("Choose a way to play:");
			System.out.println("Enter 1 to: Play a single quick game of EINS against 5 computer players of REGULAR difficulty!");
			System.out.println("Enter 2 to: Play a single quick game of EINS against 5 computer players of BEST difficulty!");
			System.out.println("Enter 3 to: Play a 3-game series of EINS against 5 computer players of REGULAR difficulty!");
			System.out.println("Enter 4 to: Play a 3-game series of EINS against 5 computer players of BEST difficulty!");
			System.out.println("Enter 5 to: Play an ultimate 7-game tournament against 5 computer players of BEST difficulty!");
			
			try {
				System.out.flush();
				userInput = scanner.nextInt();
			} catch(InputMismatchException ime) {
				System.out.println("Only type in a number, nothing else!");
				userInput = -1;
			} catch(NoSuchElementException nsee) {
				System.out.println("NSEE: " + nsee + "\nuserInput = " + userInput);
				userInput = -1;
			} finally {
				scanner = new Scanner(System.in);
			}
			
			if(userInput > 0 && userInput < 6) {
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
			reps = 7;
			diff = AIDifficulty.BEST;
		}
		
		ScoreList slist = new ScoreList();
		
		for(int i = 0; i < reps; i++) {
			System.out.println("Game #" + (i+1) + ":\n");
			Table table = new Table(nname, 5, diff);
			
			if(table.mainLoop(slist)) { // someone requested we finish early if true
				slist.printScores();
				break;
			} else {
				slist.printScores();
			}
			
		}
		
		System.out.println("Final Scores:\n");
		slist.printScoresFinal();
		
		System.out.println("\nThanks for playing!");
		scanner.close();
	}
}
