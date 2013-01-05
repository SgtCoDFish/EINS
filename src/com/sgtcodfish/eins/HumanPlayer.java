package com.sgtcodfish.eins;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

import com.sgtcodfish.eins.Card.CardColour;

/**
 * Defines a human player. Usually handled by {@link Table the Table class}.
 * @author Ashley Davis (SgtCoDFish).
 */
public class HumanPlayer extends CardEntity {
	protected String name;
	
	/**
	 * Creates a player with the name nname
	 * @param nname The name to use for the player.
	 */
	public HumanPlayer(String nname) {
		super(nname);
	}
	
	/**
	 * Executes a turn by asking the player for appropriate input to choose a move to make.
	 */
	@SuppressWarnings("resource")
	@Override
	public void doTurn() {
		Scanner scanner = new Scanner(System.in);
		Vector<Card> legal = null;
		int clooper = 1;
		int lsize = -1;
		int userInput = -1;
		boolean reprint = true; // true if the player's hand might've been changed since they last saw it and they'll need to have it printed again.
		
		System.out.println("~~~~~");
		
		do {
			if(reprint) {
				// work out what options the player has in terms of moves to make.
				// first, work out how many different cards the player can play, if any.
				if(legal != null) legal.clear();
				legal = new Vector<Card>();
				for(Card c : cards) {
					if(table.isLegal(c)) {
						// if the above are all true, this card isn't legal to play.
						legal.add(c);
					}
				}
				
				// we now have a list of legal moves, and we need to output them if we have at least one, or failing that give the player the option to draw a card.
				printHand();
				System.out.println();
				
				//System.out.println("Current card in play: " + table.getPile().getTopCard());
				lsize = legal.size(); // for speed reasons
				
				reprint = false;
			}
			
			clooper = 1;
			if(lsize >= 1) {
				System.out.println("Your options: ");
				for(Card c : legal) {
					System.out.println("Enter " + clooper + " to: Play your " + c + ".");
					clooper++;
				}
			} else {
				System.out.println("You have no legal cards!\nEnter 1 to: Draw a card.");
			}
			
			System.out.println("Enter " + (lsize + 2) + " to: Shout EINS!");
			System.out.println("Enter " + (lsize + 3) + " to: Accuse players of forgetting to shout EINS!");
			System.out.println("Enter " + (lsize + 4) + " to: See a list of current players and how many cards they have!");
			System.out.println("Enter " + (lsize + 5) + " to: Quit the game.");
			
			userInput = -1;
			
			System.out.println("Enter a number corresponding to the above menu of choices. For help, enter 0.");
			System.out.flush();
			
			try {
				if(scanner.hasNextInt()) {
					userInput = scanner.nextInt();
				} else {
					scanner = new Scanner(System.in);
				}
			} catch(InputMismatchException ime) {
				System.out.println("Only type in a number, nothing else!");
				userInput = -1;
			} catch(NoSuchElementException nsee) {
				System.out.println("NSEE: " + nsee);
				System.out.println("userInput = " + userInput);
				userInput = -1;
			} finally {
//				scanner.close();
				//scanner = new Scanner(System.in);
			}
			
			System.out.flush();
		
			if(userInput == 1 && lsize == 0) {
				break;
			}
			
			if(userInput < 0 || userInput > (lsize + 5)) {
				System.out.println("Enter a number between 0 and " + (lsize + 5) + ", not including " + (lsize + 1) + "!");
			} else if(userInput == 0) {
				System.out.println("Choose one of the options shown above. For example, enter " + (lsize + 2) + " to shout EINS!");
			} else if(userInput == lsize + 2) {
				// The user chose to shout eins, which means we handle that and let them enter another menu option.
				int bcc = countCards();
				
				sayEins();
				
				if(countCards() > bcc) {
					reprint = true; // picked up some cards so need to print our new hand
					try { Thread.sleep(1000); } catch(InterruptedException ie) {}
				}
				userInput = -1;
			} else if(userInput == lsize + 3) {
				// The user chose to accuse the last player of forgetting to shout eins, so we handle that and let them choose a new menu option.
				int bcc = countCards();
				
				accuseEins();
				
				if(bcc < countCards()) {
					reprint = true;
					try {Thread.sleep(1000); } catch(InterruptedException ie) {}
				}
				
				userInput = -1;
			} else if(userInput == lsize + 4) {
				// The user wants to see a list of current players.
				System.out.println();
				table.printPlayers();
				System.out.println();
				userInput = -1;
			} else if(userInput == lsize + 5) {
				// Need to quit!
				table.requestEnd();
//				scanner.close();
				return;
			} else if(userInput == (lsize + 1)) {
				userInput = -1;
			} else {
				break;
			}
		} while(true);
		
		if(lsize >= 1) { // if there are legal moves, to get to this point the player must have chosen to make one of them, so make it
			// the user chose to play a card
			playCard(legal.elementAt(userInput-1));
		} else { // if there aren't legal moves, all there is left is to draw.
			Card drawn = takeCard(table.getDeck());
			System.out.println(getName() + " drew " + drawn + "!");
			if(table.isLegal(drawn)) {
				// need to show another menu to allow for human players to say EINS
				int secondInput = -1;
				//scanner = new Scanner(System.in);
				
				do {
					System.out.println("\nChoose what to do:");
					System.out.println("Enter 1 to: Play the " + drawn + " you just picked up!");
					System.out.println("Enter 2 to: Shout EINS!");
					
					try {
						secondInput = scanner.nextInt();
					} catch(InputMismatchException ime) {
						System.out.println("Only type in a number, nothing else!");
						secondInput = -1;
					} catch(NoSuchElementException nsee) {
						System.out.println("NSEE: " + nsee);
						System.out.println("userInput = " + secondInput);
						secondInput = -1;
					} finally {
						scanner = new Scanner(System.in);
					}
					
					if(secondInput == 1) {
						break;
					} else if(secondInput == 2) {
						sayEins();
					}
					
				} while(true);
				
				//System.out.println(getName() + " plays the drawn " + drawn + "!");
				playCard(drawn);
			} else {
				playCard(null);
			}
		}
		
		System.out.println("~~~~~");
//		scanner.close();
	}
	
	/**
	 * This implementation asks the user for a command-line input, and takes the first character of whatever they choose to input and checks if it matches a colour.
	 * If so, it returns that colour. If not, it asks again.
	 * For example, input "foo bar" will ask again, while "bar foo" will return CardColour.BLUE.
	 */
	@Override
	public CardColour askForColour() {
		Scanner input = new Scanner(System.in);
		String ncol = new String();
		String first = new String();
		
		do {
			System.out.println("Input the colour you want to change to. Either (R)ed, (G)reen, (B)lue, or (Y)ellow. Only the first character of your input will be considered.");
			
			try {
				ncol = input.nextLine();
			} catch(InputMismatchException ime) {
				ncol = "";
			}
			
			first = Character.toString(ncol.charAt(0));
			
			if(first.compareToIgnoreCase("R") == 0 || first.compareToIgnoreCase("G") == 0 || first.compareToIgnoreCase("B") == 0 || first.compareToIgnoreCase("Y") == 0) break;
		} while(true);
		
		CardColour choice = CardColour.BLACK; // if we actually return BLACK something has gone horifically wrong.
		
		if(first.compareToIgnoreCase("R") == 0) {
			choice = CardColour.RED;
		} else if(first.compareToIgnoreCase("B") == 0) {
			choice = CardColour.BLUE;
		} else if(first.compareToIgnoreCase("G") == 0) {
			choice = CardColour.GREEN;
		} else if(first.compareToIgnoreCase("Y") == 0) {
			choice = CardColour.YELLOW;
		}
		
		//System.out.println("Choice was: " + choice);
		input.close();
		
		return choice;
	}
	
	/**
	 * Returns a string identifying this entity as a human
	 */
	@Override
	public String getSubclassIdentifier() {
		return "[HUMAN]";
	}
}
